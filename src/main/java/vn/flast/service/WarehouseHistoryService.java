package vn.flast.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.flast.controller.common.BaseController;
import vn.flast.entities.WareHouseItem;
import vn.flast.entities.WarehouseHistoryFilter;
import vn.flast.models.WareHouseHistory;
import vn.flast.models.Warehouse;
import vn.flast.pagination.Ipage;
import vn.flast.repositories.ProductRepository;
import vn.flast.repositories.ProductSkusRepository;
import vn.flast.repositories.WareHouseHistoryRepository;
import vn.flast.repositories.WareHouseStatusRepository;
import vn.flast.repositories.WarehouseRepository;
import vn.flast.repositories.WarehouseStockRepository;
import vn.flast.utils.Common;
import vn.flast.utils.CopyProperty;
import vn.flast.utils.EntityQuery;
import vn.flast.utils.JsonUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseHistoryService {

    @PersistenceContext
    protected EntityManager entityManager;

    private final WareHouseHistoryRepository wareHouseHistoryRepository;

    private final WarehouseRepository warehouseRepository;

    private final WarehouseStockRepository warehouseStockRepository;

    private final ProductRepository productRepository;

    private final ProductSkusRepository productSkusRepository;
    private final BaseController baseController;

    private final WareHouseStatusRepository wareHouseStatusRepository;

    @Transactional(rollbackFor = Exception.class)
    public WareHouseHistory created(WareHouseHistory input) {
        var stock = warehouseStockRepository.findById(input.getStockId()).orElseThrow(
                () -> new RuntimeException("record does not exist.")
        );
        // Tạo mã lệnh nhập kho (code)
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = wareHouseHistoryRepository.countByCreatedDate(LocalDate.now());
        String code = String.format("WH-%s-%04d", today, count + 1);
        input.setCode(code); // Gán mã lệnh nhập kho vào input
        input.setStockName(stock.getName());
        input.setInfo(JsonUtils.toJson(input.getItems()));
        return wareHouseHistoryRepository.save(input);
    }


    @Transactional(rollbackFor = Exception.class)
    public WareHouseHistory updated(WareHouseHistory input) {
        var warehouseHistory = wareHouseHistoryRepository.findById(input.getId()).orElseThrow(
                () -> new RuntimeException("Bản ghi không tồn tại !")
        );
        var stock = warehouseStockRepository.findById(input.getStockId()).orElseThrow(
                () -> new RuntimeException("record does not exist.")
        );
        var user = baseController.getInfo();
        boolean isAdminOrManager = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        var statusConfirm = wareHouseStatusRepository.findByType().getId();
        if(isAdminOrManager && input.getStatus().equals(statusConfirm)){
            for (WareHouseItem item : input.getItems()) {
                item.setProviderId(input.getProviderId());
                var warehouseOld = warehouseRepository.findProductStock(input.getProviderId(), item.getProductId(), item.getSkuId(), input.getStockId());
                Warehouse warehouse = new Warehouse();

                if (warehouseOld == null) {
                    CopyProperty.CopyIgnoreNull(item, warehouse);
                    var skuProduct = productSkusRepository.findById(item.getSkuId()).orElseThrow(
                            () -> new RuntimeException("Sku does not exist!")
                    );
                    warehouse.setTotal(item.getQuantity());
                    warehouse.setStockId(input.getStockId());
                    warehouse.setStockName(stock.getName());
                    warehouse.setSkuInfo(item.getSkuInfo());
                    warehouse.setSkuName(skuProduct.getName());
                    warehouseRepository.save(warehouse);
                    input.setWarehouserId(warehouse.getId());
                } else {
                    warehouseOld.setTotal(warehouseOld.getTotal() + item.getQuantity());
                    warehouseRepository.save(warehouseOld);
                    input.setWarehouserId(warehouseOld.getId());
                }
            }
            input.setStatusConfirm(WareHouseHistory.CONFIRM_WAREHOUSE);
            return wareHouseHistoryRepository.save(input);
        }
        else{
            CopyProperty.CopyIgnoreNull(input, warehouseHistory);
            return wareHouseHistoryRepository.save(warehouseHistory);
        }
    }

    public Ipage<?> fetch(WarehouseHistoryFilter filter) {
        var et = EntityQuery.create(entityManager, WareHouseHistory.class);
        et.integerEqualsTo("status", filter.getStatus());
        et.longEqualsTo("productId", filter.getProductId());
        et.longEqualsTo("providerId", filter.getProviderId());
        et.between("inTime", filter.getFrom(), filter.getTo());
        et.addDescendingOrderBy("id");
        et.setMaxResults(filter.getLimit()).setFirstResult(filter.getLimit() * filter.page());
        var lists = et.list();
        lists.forEach(item -> item.setItems(JsonUtils.Json2ListObject(item.getInfo(), WareHouseItem.class)));
        return Ipage.generator(filter.getLimit(), et.count(), filter.page(), lists);
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id){
        var data = wareHouseHistoryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Bản ghi không tồn tại !")
        );
        wareHouseHistoryRepository.delete(data);
    }
}
