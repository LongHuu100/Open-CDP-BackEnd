package vn.flast.domains.order;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.flast.controller.BaseController;
import vn.flast.domains.payments.PayService;
import vn.flast.entities.order.OrderCare;
import vn.flast.entities.order.OrderComment;
import vn.flast.entities.order.OrderDetail;
import vn.flast.entities.order.OrderResponse;
import vn.flast.exception.ResourceNotFoundException;
import vn.flast.models.CustomerOrder;
import vn.flast.models.CustomerOrderDetail;
import vn.flast.models.CustomerOrderNote;
import vn.flast.models.CustomerOrderStatus;
import vn.flast.models.Data;
import vn.flast.orchestration.EventDelegate;
import vn.flast.orchestration.EventTopic;
import vn.flast.orchestration.Message;
import vn.flast.orchestration.MessageInterface;
import vn.flast.orchestration.Publisher;
import vn.flast.pagination.Ipage;
import vn.flast.repositories.CustomerOrderDetailRepository;
import vn.flast.repositories.CustomerOrderNoteRepository;
import vn.flast.repositories.CustomerOrderRepository;
import vn.flast.repositories.CustomerOrderStatusRepository;
import vn.flast.repositories.CustomerPersonalRepository;
import vn.flast.repositories.DataRepository;
import vn.flast.searchs.OrderFilter;
import vn.flast.service.DataService;
import vn.flast.utils.Common;
import vn.flast.utils.CopyProperty;
import vn.flast.utils.EntityQuery;
import vn.flast.utils.NumberUtils;
import vn.flast.utils.SqlBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service("orderService")
@Log4j2
public class OrderService  implements Publisher, Serializable {

    private EventDelegate eventDelegate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerOrderRepository orderRepository;

    @Autowired
    private CustomerPersonalRepository customerRepository;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private CustomerOrderDetailRepository detailRepository;

    @Autowired
    private BaseController baseController;

    @Autowired
    private CustomerOrderStatusRepository statusOrderRepository;

    @Autowired
    private CustomerOrderNoteRepository orderNoteRepository;

    @Autowired
    @Lazy
    private PayService payService;

    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder saveOpportunity(OrderInput input) {

        var order = new CustomerOrder();
        order.setCode(OrderUtils.createOrderCode());
        order.setUserCreateUsername(Common.getSsoId());
        order.setUserCreateId(Common.getUserId());
        input.transformOrder(order);

        List<Long> removedIds = new ArrayList<>();
        if(NumberUtils.isNotNull(order.getId())) {
            var entity = orderRepository.findById(order.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Not Found Order .!")
            );
            if(Common.CollectionIsEmpty(input.details())) {
                throw new RuntimeException("Lỗi sửa chưa cập nhật đơn ! ");
            }
            List<Long> detailInputIds = input.details().stream()
                .map(OrderDetail::getDetailId)
                .filter(NumberUtils::isNotNull).toList();
            List<Long> detailIds = entity.getDetails().stream().map(CustomerOrderDetail::getId).toList();

            removedIds = detailIds.stream().filter(id -> !detailInputIds.contains(id)).toList();
            Iterator<CustomerOrderDetail> iterator = entity.getDetails().iterator();
            while (iterator.hasNext()) {
                CustomerOrderDetail detail = iterator.next();
                if (removedIds.contains(detail.getId())) {
                    iterator.remove();
                }
            }
            CopyProperty.CopyIgnoreNull(entity, order);
        } else {
            Data data = dataRepository.findFirstByPhone(input.customer().getMobile()).orElseThrow(
                () -> new ResourceNotFoundException("Lead Not Found .!")
            );
            data.setStatus(DataService.DATA_STATUS.THANH_CO_HOI.getStatusCode());
            dataRepository.save(data);

            order.setDataId(data.getId());
            order.setSource(data.getSource());
            order.setPaid(0.0);
        }
        order.setStatus(statusOrderRepository.findStartOrder().getId());

        var listDetails = input.transformOrderDetail(order, order.getStatus());
        if (!removedIds.isEmpty()) {
            Set<Long> removedIdSet = new HashSet<>(removedIds);
            listDetails.removeIf(detail -> removedIdSet.contains(detail.getId()));
        }
        order.setDetails(listDetails);
        OrderUtils.calculatorPrice(order);

        /* CascadeType.ALL thì không cần detail save */
        orderRepository.save(order);
        listDetails.forEach(detail -> detail.setCustomerOrder(order));
        listDetails.forEach(detail -> detail.setCustomerOrderId(order.getId()));

        this.sendMessageOnOrderChange(order);
        return order;
    }

    public Ipage<?>fetchList(OrderFilter filter) {
        var sale = baseController.getInfo();
        Integer page = filter.page();
        var et = EntityQuery.create(entityManager, CustomerOrder.class);
        boolean isAdminOrManager = sale.getAuthorities().stream().anyMatch(auth
            -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALE_MANAGER")
        );
        Integer userCreateId = (filter.saleId() != null) ?
            (isAdminOrManager ? filter.saleId() : sale.getId()) :
            (isAdminOrManager ? null : sale.getId());

        et.integerEqualsTo("userCreateId", userCreateId);
        et.like("customerName", filter.customerName())
            .integerEqualsTo("customerId", filter.customerId())
            .like("customerMobile", filter.customerPhone())
            .like("customerEmail", filter.customerEmail())
            .like("code", filter.code())
            .addDescendingOrderBy("createdAt")
            .stringEqualsTo("type", filter.type())
            .setMaxResults(filter.limit())
            .setFirstResult(page * filter.limit());
        var lists = transformDetails(et.list());
        return Ipage.generator(filter.limit(), et.count(), page, lists);
    }

    public Ipage<?>fetchListCoHoiNotCare(OrderFilter filter) {
        var sale = baseController.getInfo();
        boolean isAdminOrManager = sale.getAuthorities().stream().anyMatch(auth
            -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_CSKH")
        );
        Integer userCreateId = (filter.saleId() != null) ?
            (isAdminOrManager ? filter.saleId() : sale.getId()) :
            (isAdminOrManager ? null : sale.getId());

        int LIMIT = filter.limit();
        int OFFSET = filter.page() * LIMIT;

        final String totalSQL = " FROM `customer_order` c left join `customer_order_note` n on c.code = n.order_code ";
        SqlBuilder sqlBuilder = SqlBuilder.init(totalSQL);
        sqlBuilder.addIntegerEquals("c.user_create_id", userCreateId);
        sqlBuilder.addStringEquals("c.type", CustomerOrder.TYPE_CO_HOI);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date dayBeforeYesterday = calendar.getTime();
        sqlBuilder.addDateLessThan("c.created_at", dayBeforeYesterday);

        sqlBuilder.like("c.customer_name", filter.customerName());
        sqlBuilder.addIntegerEquals("c.customer_id", filter.customerId());
        sqlBuilder.like("c.customer_mobile", filter.customerPhone());
        sqlBuilder.like("c.customer_email", filter.customerEmail());
        sqlBuilder.like("c.code", filter.code());
        sqlBuilder.addIsEmpty("n.order_code");

        String finalQuery = sqlBuilder.builder();
        var countQuery = entityManager.createNativeQuery(sqlBuilder.countQueryString());
        Long count = sqlBuilder.countOrSumQuery(countQuery);

        var nativeQuery = entityManager.createNativeQuery("SELECT c.* " + finalQuery + " ORDER BY c.created_at DESC" , CustomerOrder.class);
        nativeQuery.setMaxResults(LIMIT);
        nativeQuery.setFirstResult(OFFSET);

        var listData = EntityQuery.getListOfNativeQuery(nativeQuery, CustomerOrder.class);
        var lists = transformDetails(listData);
        return Ipage.generator(LIMIT, count, filter.page(), lists);
    }

    public List<OrderComment> fetchListOrderStatus(OrderFilter filter) {
        var sale = baseController.getInfo();
        var et = EntityQuery.create(entityManager, CustomerOrder.class);
        boolean isAdminOrManager = sale.getAuthorities().stream().anyMatch(auth
            -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALE_MANAGER")
        );
        Integer userCreateId = (filter.saleId() != null) ?
            (isAdminOrManager ? filter.saleId() : sale.getId()) :
            (isAdminOrManager ? null : sale.getId());

        et.integerEqualsTo("userCreateId", userCreateId);
        et.like("customerName", filter.customerName())
            .integerEqualsTo("customerId", filter.customerId())
            .like("customerMobile", filter.customerPhone())
            .like("customerEmail", filter.customerEmail())
            .like("code", filter.code())
            .dateIsNull("doneAt")
            .addDescendingOrderBy("createdAt")
            .stringEqualsTo("type", filter.type());

        var lists = transformDetails(et.list());
        return lists.stream().map(order -> {
            OrderComment op = new OrderComment();
            CopyProperty.CopyIgnoreNull(order, op);
            op.setNotes(orderNoteRepository.findByOrderCode(order.getCode()));
            return op;
        }).toList();
    }

    public CustomerOrder completeOrder(Long id) {
        CustomerOrder order = orderRepository.findById(id).orElseThrow(
            () -> new RuntimeException("error no record exists")
        );
        if (order.getType().equals(CustomerOrder.TYPE_CO_HOI)) {
            throw new RuntimeException("Không thể hoàn thành đơn hàng cơ hội. Vui lòng cập nhật trạng thái đơn hàng cơ hội thông qua chức năng khác.");
        }
        order.setDoneAt(new Date());
        orderRepository.save(order);
        return order;
    }

    public Ipage<?> fetchListCoHoiCare(OrderFilter filter) {
        var sale = baseController.getInfo();
        boolean isAdminOrManager = sale.getAuthorities().stream().anyMatch( auth
            -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_CSKH")
        );
        Integer userCreateId = (filter.saleId() != null) ?
            (isAdminOrManager ? filter.saleId() : sale.getId()) :
            (isAdminOrManager ? null : sale.getId());

        int LIMIT = filter.limit();
        int OFFSET = filter.page() * LIMIT;

        final String totalSQL = " FROM `customer_order` c left join `customer_order_note` n on c.code = n.order_code ";
        SqlBuilder sqlBuilder = SqlBuilder.init(totalSQL);
        sqlBuilder.addIntegerEquals("c.user_create_id", userCreateId);
        sqlBuilder.addStringEquals("c.type", CustomerOrder.TYPE_CO_HOI);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date dayBeforeYesterday = calendar.getTime();
        sqlBuilder.addDateLessThan("c.created_at", dayBeforeYesterday);

        sqlBuilder.like("c.customer_name", filter.customerName());
        sqlBuilder.addIntegerEquals("c.customer_id", filter.customerId());
        sqlBuilder.like("c.customer_mobile", filter.customerPhone());
        sqlBuilder.like("c.customer_email", filter.customerEmail());
        sqlBuilder.like("c.code", filter.code());
        sqlBuilder.addIntegerEquals("n.type", CustomerOrderNote.TYPE_COHOI);

        String finalQuery = sqlBuilder.builder();
        var countQuery = entityManager.createNativeQuery(sqlBuilder.countQueryString());
        Long count = sqlBuilder.countOrSumQuery(countQuery);

        var nativeQuery = entityManager.createNativeQuery("SELECT c.* " + finalQuery + " ORDER BY c.created_at DESC" , CustomerOrder.class);
        nativeQuery.setMaxResults(LIMIT);
        nativeQuery.setFirstResult(OFFSET);

        var listData = EntityQuery.getListOfNativeQuery(nativeQuery, CustomerOrder.class);
        var lists = transformDetails(listData);
        return Ipage.generator(LIMIT, count, filter.page(), lists);
    }

    public Ipage<?>fetchLisOrderNotCare(OrderFilter filter) {
        var sale = baseController.getInfo();
        boolean isAdminOrManager = sale.getAuthorities().stream() .anyMatch(auth
            -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_CSKH")
        );
        Integer userCreateId = (filter.saleId() != null) ?
            (isAdminOrManager ? filter.saleId() : sale.getId()) :
            (isAdminOrManager ? null : sale.getId());

        int LIMIT = filter.limit();
        int OFFSET = filter.page() * LIMIT;

        final String totalSQL = " FROM `customer_order` c left join `customer_order_note` n on c.code = n.order_code ";
        SqlBuilder sqlBuilder = SqlBuilder.init(totalSQL);
        sqlBuilder.addIntegerEquals("c.user_create_id", userCreateId);
        sqlBuilder.addStringEquals("c.type", CustomerOrder.TYPE_ORDER);
        sqlBuilder.like("c.customer_name", filter.customerName());
        sqlBuilder.addIntegerEquals("c.customer_id", filter.customerId());
        sqlBuilder.like("c.customer_mobile", filter.customerPhone());
        sqlBuilder.like("c.customer_email", filter.customerEmail());
        sqlBuilder.like("c.code", filter.code());
        sqlBuilder.addIsEmpty("n.order_code");

        String finalQuery = sqlBuilder.builder();
        var countQuery = entityManager.createNativeQuery(sqlBuilder.countQueryString());
        Long count = sqlBuilder.countOrSumQuery(countQuery);

        var nativeQuery = entityManager.createNativeQuery("SELECT c.* " + finalQuery + " ORDER BY c.created_at DESC" , CustomerOrder.class);
        nativeQuery.setMaxResults(LIMIT);
        nativeQuery.setFirstResult(OFFSET);

        var listData = EntityQuery.getListOfNativeQuery(nativeQuery, CustomerOrder.class);
        var lists = transformDetails(listData);
        return Ipage.generator(LIMIT, count, filter.page(), lists);
    }

    public Ipage<?>fetchLisOrderCancel(OrderFilter filter) {
        var sale = baseController.getInfo();
        boolean isAdminOrManager = sale.getAuthorities().stream().anyMatch(auth
            -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_CSKH")
        );
        Integer userCreateId = (filter.saleId() != null) ?
            (isAdminOrManager ? filter.saleId() : sale.getId()) :
            (isAdminOrManager ? null : sale.getId());

        int LIMIT = filter.limit();
        int OFFSET = filter.page() * LIMIT;

        final String totalSQL = " FROM `customer_order` c left join `customer_order_note` n on c.code = n.order_code ";
        SqlBuilder sqlBuilder = SqlBuilder.init(totalSQL);
        sqlBuilder.addIntegerEquals("c.user_create_id", userCreateId);
        sqlBuilder.addStringEquals("c.type", CustomerOrder.TYPE_ORDER);
        sqlBuilder.like("c.customer_name", filter.customerName());
        sqlBuilder.addIntegerEquals("c.customer_id", filter.customerId());
        sqlBuilder.like("c.customer_mobile", filter.customerPhone());
        sqlBuilder.like("c.customer_email", filter.customerEmail());
        sqlBuilder.like("c.code", filter.code());
        sqlBuilder.addIsEmpty("n.order_code");
        sqlBuilder.addIntegerEquals("n.type", CustomerOrderNote.TYPE_ORDER);

        String finalQuery = sqlBuilder.builder();
        var countQuery = entityManager.createNativeQuery(sqlBuilder.countQueryString());
        Long count = sqlBuilder.countOrSumQuery(countQuery);

        var nativeQuery = entityManager.createNativeQuery("SELECT c.* " + finalQuery + " ORDER BY c.created_at DESC" , CustomerOrder.class);
        nativeQuery.setMaxResults(LIMIT);
        nativeQuery.setFirstResult(OFFSET);

        var listData = EntityQuery.getListOfNativeQuery(nativeQuery, CustomerOrder.class);
        var lists = transformDetails(listData);
        return Ipage.generator(LIMIT, count, filter.page(), lists);
    }

    public Ipage<?>fetchLisOrderCare(OrderFilter filter) {
        var sale = baseController.getInfo();
        boolean isAdminOrManager = sale.getAuthorities().stream().anyMatch(auth
            -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_CSKH")
        );
        Integer userCreateId = (filter.saleId() != null) ?
            (isAdminOrManager ? filter.saleId() : sale.getId()) :
            (isAdminOrManager ? null : sale.getId());

        int LIMIT = filter.limit();
        int OFFSET = filter.page() * LIMIT;

        final String totalSQL = " FROM `customer_order` c left join `customer_order_note` n on c.code = n.order_code ";
        SqlBuilder sqlBuilder = SqlBuilder.init(totalSQL);
        sqlBuilder.addIntegerEquals("c.user_create_id", userCreateId);
        sqlBuilder.addStringEquals("c.type", CustomerOrder.TYPE_ORDER);
        sqlBuilder.like("c.customer_name", filter.customerName());
        sqlBuilder.addIntegerEquals("c.customer_id", filter.customerId());
        sqlBuilder.like("c.customer_mobile", filter.customerPhone());
        sqlBuilder.like("c.customer_email", filter.customerEmail());
        sqlBuilder.like("c.code", filter.code());
        sqlBuilder.addNotNUL("n.order_code");
        sqlBuilder.addIntegerEquals("n.type", CustomerOrderNote.TYPE_ORDER);

        String finalQuery = sqlBuilder.builder();
        var countQuery = entityManager.createNativeQuery(sqlBuilder.countQueryString());
        Long count = sqlBuilder.countOrSumQuery(countQuery);

        var nativeQuery = entityManager.createNativeQuery("SELECT c.* " + finalQuery + " ORDER BY c.created_at DESC" , CustomerOrder.class);
        nativeQuery.setMaxResults(LIMIT);
        nativeQuery.setFirstResult(OFFSET);

        var listData = EntityQuery.getListOfNativeQuery(nativeQuery, CustomerOrder.class);
        var lists = transformDetails(listData);
        return Ipage.generator(LIMIT, count, filter.page(), lists);
    }

    public List<CustomerOrder> transformDetails(List<CustomerOrder> orders) {
        if(Common.CollectionIsEmpty(orders)) {
            return new ArrayList<>();
        }
        var newOrders = orders.stream().map(CustomerOrder::cloneNoDetail).toList();
        var orderIds = newOrders.stream().map(CustomerOrder::getId).toList();
        var details = detailRepository.fetchDetailOrdersId(orderIds);
        for( CustomerOrder order : newOrders ) {
            var detailOfOrder = details.stream().filter(
                d -> d.getCustomerOrderId().equals(order.getId())
            );
            order.setDetails(detailOfOrder.toList());
        }
        return newOrders;
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder updateStatusOrder(Long orderId, Integer statusId) {
        CustomerOrder order = orderRepository.findById(orderId).orElseThrow(
            () -> new RuntimeException("error no record exists")
        );
        if (order.getType().equals(CustomerOrder.TYPE_CO_HOI)) {
            throw new RuntimeException("Không thể cập nhật trạng thái vì đơn hàng là cơ hội. Vui lòng cập nhật trạng thái đơn hàng cơ hội thông qua chức năng khác.");
        }
        CustomerOrderStatus status = statusOrderRepository.findById(statusId).orElseThrow(
            () -> new RuntimeException("error no record exists")
        );
        order.setStatus(status.getId());
        orderRepository.save(order);
        this.sendMessageOnOrderChange(order);
        return order;
    }

    public void sendMessageOnOrderChange(CustomerOrder order) {
        var message = Message.create(EventTopic.ORDER_CHANGE, order.clone());
        this.publish(message);
    }

    @Transactional
    public OrderResponse view(Long id) {
        var order = orderRepository.fetchWithCustomer(id).orElseThrow(
            () -> new ResourceNotFoundException("Order not found .!")
        );
        return withOrderDetail(order);
    }

    @Transactional
    public OrderResponse withOrderDetail(CustomerOrder order) {
        Hibernate.initialize(order.getDetails());
        var orderRep = new OrderResponse();
        CopyProperty.CopyNormal(order.cloneNoDetail(), orderRep);
        orderRep.setDetails(new ArrayList<>(order.getDetails()));
        orderRep.setCustomer(customerRepository.findById(orderRep.getCustomerId()).orElseThrow(
            () -> new RuntimeException("Customer not found !")
        ));
        return orderRep;
    }

    @Transactional
    public OrderResponse findByCode(String code) {
        var order = orderRepository.findByCode(code).orElseThrow(
            () -> new ResourceNotFoundException("Order not found .!")
        );
        return withOrderDetail(order);
    }

    @Transactional
    public OrderResponse findById(Long id) {
        var order = orderRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Order not found .!")
        );
        log.info(order.getCustomerId());
        return withOrderDetail(order);
    }

    @Override
    public void setDelegate(EventDelegate eventDelegate) {
        this.eventDelegate = eventDelegate;
    }

    @Override
    public void publish(MessageInterface message) {
        if(Objects.nonNull(eventDelegate)) {
            eventDelegate.sendEvent(message);
        }
    }

    @Transactional
    public void cancelCoHoi(Long orderId, Boolean isDetail){
        Integer statusCancel = statusOrderRepository.findCancelOrder().getId();
        if(isDetail) {
            var order = detailRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("error no record exists")
            );
            order.setStatus(statusCancel);
        } else {
            var order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("error no record exists")
            );
            order.setStatus(statusCancel);
        }
    }

    public CustomerOrderNote takeCareNoteCoHoi(OrderCare input) {
        orderRepository.findByCode(input.getOrderCode()).orElseThrow(
            () -> new RuntimeException("error no record exists")
        );
        var care = orderNoteRepository.findByOrderCode(input.getOrderCode());
        if (care == null) {
            var noteOrder = new CustomerOrderNote();
            CopyProperty.CopyIgnoreNull(input, noteOrder);
            noteOrder.setUserName(baseController.getInfo().getSsoId());
            noteOrder.setUsesId(baseController.getInfo().getId());
            return orderNoteRepository.save(noteOrder);
        } else {
            CopyProperty.CopyIgnoreNull(input, care);
            return orderNoteRepository.save(care);
        }
    }
}
