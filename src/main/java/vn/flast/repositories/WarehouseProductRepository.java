package vn.flast.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.flast.models.WarehouseProduct;
import java.util.List;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, Integer> {

    @Query("FROM WarehouseProduct w WHERE w.productId = :productId")
    List<WarehouseProduct> findByProductId(Long productId);

    @Query("FROM WarehouseProduct w WHERE w.skuId = :skuId AND stockId = :stockId")
    List<WarehouseProduct> findBySkuAndStockId(Long skuId, Integer stockId);
}
