package vn.flast.domains.order;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.flast.controller.common.BaseController;
import vn.flast.domains.payments.OrderPaymentInfo;
import vn.flast.domains.payments.PayService;
import vn.flast.entities.OrderCare;
import vn.flast.entities.OrderResponse;
import vn.flast.entities.OrderStatus;
import vn.flast.entities.ReceivableFilter;
import vn.flast.exception.ResourceNotFoundException;
import vn.flast.models.CustomerOrder;
import vn.flast.models.CustomerOrderDetail;
import vn.flast.models.CustomerOrderNote;
import vn.flast.models.CustomerPersonal;
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
import vn.flast.repositories.CustomerPersonalRepository;
import vn.flast.repositories.DataRepository;
import vn.flast.repositories.DetailItemRepository;
import vn.flast.repositories.StatusOrderRepository;
import vn.flast.searchs.OrderFilter;
import vn.flast.service.DataService;
import vn.flast.utils.BeanUtil;
import vn.flast.utils.Common;
import vn.flast.utils.CopyProperty;
import vn.flast.utils.EntityQuery;
import vn.flast.utils.NumberUtils;
import vn.flast.utils.SqlBuilder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

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
    private StatusOrderRepository statusOrderRepository;

    @Autowired
    private DetailItemRepository detailItemRepository;

    @Autowired
    private CustomerOrderNoteRepository orderNoteRepository;

    public Ipage<?>fetchList(OrderFilter filter) {
        var sale = baseController.getInfo();
        Integer page = filter.page();
        var et = EntityQuery.create(entityManager, CustomerOrder.class);
        boolean isAdminOrManager = sale.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_SALE_MANAGER"));
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
        Integer page = filter.page();
        boolean isAdminOrManager = sale.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_CSKH"));
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

    public Ipage<?>fetchListCoHoiCare(OrderFilter filter) {
        var sale = baseController.getInfo();
        Integer page = filter.page();
        boolean isAdminOrManager = sale.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_CSKH"));
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
        Integer page = filter.page();
        boolean isAdminOrManager = sale.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_CSKH"));
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

    public Ipage<?>fetchLisOrderCancel(OrderFilter filter) {
        var sale = baseController.getInfo();
        boolean isAdminOrManager = sale.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_CSKH"));
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
        Integer page = filter.page();
        boolean isAdminOrManager = sale.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_CSKH"));
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
        transformItems(details);
        for( CustomerOrder order : newOrders ) {
            var detailOfOrder = details.stream().filter(
                d -> d.getCustomerOrderId().equals(order.getId())
            );
            order.setDetails(detailOfOrder.toList());
        }
        return newOrders;
    }

    public List<CustomerOrderDetail> transformItems(List<CustomerOrderDetail> details){
        if(Common.CollectionIsEmpty(details)) {
            return new ArrayList<>();
        }

        var detailIds = details.stream().map(CustomerOrderDetail::getId).toList();
        var items = detailItemRepository.fetchDetailOrdersId(detailIds);
        for( CustomerOrderDetail detail : details ) {
            var itemOfOrder = items.stream().filter(
                    d -> d.getOrderDetailId().equals(detail.getId())
            );
            detail.setItems(itemOfOrder.toList());
        }
        return details;
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder create(OrderInput input) {
        var order = new CustomerOrder();
        order.setCode(OrderUtils.createOrderCode());
        order.setUserCreateUsername(Common.getSsoId());
        order.setUserCreateId(Common.getUserId());
        input = input.withCustomer(input.transformCustomer(input.customer()));
        input.transformOrder(order);
        if(NumberUtils.isNotNull(order.getId())) {
            var entity = orderRepository.findById(order.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Not Found Order .!")
            );
            CopyProperty.CopyIgnoreNull(entity, order);
        } else {
            OrderInput finalInput = input;
            Data data = dataRepository.findFirstByPhone(input.customer().getMobile())
                    .orElseGet(() -> {
                        Data newData = new Data();
                        newData.setCustomerMobile(finalInput.customer().getMobile());
                        newData.setSource(DataService.DATA_SOURCE.DIRECT.getSource()); // Thay đổi nếu cần
                        newData.setCustomerName(finalInput.customer().getName());
                        newData.setStaff(Common.getSsoId());
                        newData.setSaleId(Common.getUserId());
                        newData.setStatus(DataService.DATA_STATUS.THANH_CO_HOI.getStatusCode());
                        return dataRepository.save(newData);
                    });
            order.setDataId(data.getId());
            order.setSource(data.getSource());
            order.setPaid(0.);
            orderRepository.save(order);
        }
        if(order.getType().equals(CustomerOrder.TYPE_ORDER)){
            order.setStatus(statusOrderRepository.findStartOrder().getId());
        }
        var listDetails = input.transformOnCreateDetail(order);
        order.setDetails(listDetails);
        detailRepository.saveAll(listDetails);
        detailItemRepository.saveAll(input.transformOnCreateDetailItem(listDetails));
        OrderUtils.calculatorPrice(order);
        orderRepository.save(order);
        this.sendMessageOnOrderChange(order);
        if (input.paymentInfo() != null && Boolean.TRUE.equals(input.paymentInfo().status())) {
            order.setPaid(0.);
            OrderPaymentInfo updatedPaymentInfo = input.paymentInfo().withId(order.getId());
            var payService = BeanUtil.getBean(PayService.class);
            payService.manualMethod(updatedPaymentInfo);
        }
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder updateOrder(OrderInput input){
        CustomerOrder orderOld = orderRepository.findById(input.id()).orElseThrow(
                () -> new RuntimeException("error no record exists")
        );
        CustomerOrder order = new CustomerOrder();
        input = input.withCustomer(input.transformCustomer(input.customer()));
        input.transformOrder(orderOld);
        CopyProperty.CopyIgnoreNull(orderOld, order);
        var listDetails = input.transformOnCreateDetail(order);
        order.setDetails(listDetails);
        detailRepository.saveAll(listDetails);
        detailItemRepository.saveAll(input.transformOnCreateDetailItem(listDetails));
        OrderUtils.calculatorPrice(order);
        orderRepository.save(order);
        if (input.paymentInfo() != null && Boolean.TRUE.equals(input.paymentInfo().status())) {
            order.setPaid(Optional.ofNullable(input.paymentInfo()).map(OrderPaymentInfo::amount).orElse(0.));
            OrderPaymentInfo updatedPaymentInfo = input.paymentInfo().withId(order.getId());
            var payService = BeanUtil.getBean(PayService.class);
            payService.manualMethod(updatedPaymentInfo);
        }
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
        CopyProperty.CopyNormal(order.clone(), orderRep);
        orderRep.setDetails(new ArrayList<>(order.getDetails()));
        orderRep.setCustomer(customerRepository.findById(orderRep.getCustomerId()).orElse(null));
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

    public static String createOrderCode(String customerMobilePhone, String source) {
        if (customerMobilePhone == null) {
            return null;
        }
        String lastThereDigits = customerMobilePhone.substring(customerMobilePhone.length() - 3);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int day = calendar.get(Calendar.DATE);
        /* month +1 the month for current month */
        int month = calendar.get(Calendar.MONTH) + 1;
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String subYear = year.substring(year.length() - 2);
        String endCode = day + month + subYear + lastThereDigits;
        return source
                + Common.getAlphaNumericString(1, false)
                + Common.getAlphaNumericString(2, true)
                + endCode;
    }

    @Transactional
    public void cancelCohoi(Long orderId, Boolean detail){
        Integer statusCancel = statusOrderRepository.findCancelOrder().getId();
        if(detail){
            var order = detailRepository.findById(orderId).orElseThrow(
                    () -> new RuntimeException("error no record exists")
            );
            order.setStatus(statusCancel);
        }
        else {
            var order = orderRepository.findById(orderId).orElseThrow(
                    () -> new RuntimeException("error no record exists")
            );
            order.setStatus(statusCancel);
        }
    }

    public static long calTotal(CustomerOrder order) {
        try {
            Double subtotal = order.getSubtotal();
            int shippingCost = NumberUtils.numberWithDefaultZero(order.getShippingCost());
            long priceOff = NumberUtils.numberWithDefaultZero(order.getPriceOff().intValue());
            int feeSaleOther = NumberUtils.numberWithDefaultZero(order.getFeeSaleOther());
            int vatPrice = order.calVat();
            long total = (long) (subtotal + shippingCost + feeSaleOther + vatPrice);
            return (long) (total - priceOff - order.calCastBack());
        } catch (Exception ex) {
            log.info("Tính chi phí đơn hàng lỗi: {}", ex.getMessage());
            return 0;
        }
    }

    public Ipage<?> fetchReceivable(ReceivableFilter filter){
        var sale = baseController.getInfo();
        int LIMIT = filter.getLimit();
        int PAGE = filter.page();
        int OFFSET = (filter.page()) * LIMIT;
        var et = EntityQuery.create(entityManager, CustomerOrder.class);
        boolean isAdminOrManager = sale.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_SALE_MANAGER"));
        Integer userCreateId = (filter.getSaleId() != null) ?
                (isAdminOrManager ? filter.getSaleId() : sale.getId()) :
                (isAdminOrManager ? null : sale.getId());
        et.integerEqualsTo("userCreateId", userCreateId);
        et.fieldLessThan("paid", "total");
        et.like("customerName", filter.getCustomerName())
                .integerEqualsTo("customerId", filter.getCustomerId())
                .stringEqualsTo("customerMobile", filter.getCustomerPhone())
                .stringEqualsTo("code", filter.getCode())
                .addDescendingOrderBy("createdAt")
                .stringEqualsTo("type", CustomerOrder.TYPE_ORDER)
                .setMaxResults(LIMIT)
                .setFirstResult(OFFSET);

        var lists = transformDetails(et.list());
        return Ipage.generator(LIMIT, et.count(), PAGE, lists);
    }

    public CustomerOrderNote takeCareNoteCohoi(OrderCare input) {
        var order = orderRepository.findByCode(input.getOrderCode())
                .orElseThrow(() -> new RuntimeException("error no record exists"));

        var care = orderNoteRepository.findByOrderCode(input.getOrderCode());
        String currentNote = input.getNote();

        if (StringUtils.isNotEmpty(currentNote)) {
            StringBuilder sb = new StringBuilder();
            String preNote = care != null ? care.getNote() : null;

            if (StringUtils.isNotEmpty(preNote)) {
                sb.append(preNote);
            }
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            sb.append("<div class='i-note'>")
                    .append("<p class='note-edit'>")
                    .append(baseController.getInfo().getSsoId())
                    .append(" (").append(formattedDate).append(")")
                    .append("</p>")
                    .append("<div class='content-note'>").append(currentNote).append("</div>")
                    .append("</div>");
            input.setNote(sb.toString());
        }

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
