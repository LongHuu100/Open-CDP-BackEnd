package vn.flast.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.flast.models.ProductSkusPrice;
import java.util.List;

public interface ProductSkusPriceRepository extends JpaRepository<ProductSkusPrice, Integer> {

    @Query("FROM ProductSkusPrice p WHERE p.productId = :productId")
    List<ProductSkusPrice> findByProduct(Long productId);

    @Modifying
    @Query("DELETE FROM ProductSkusPrice p WHERE p.productId = :productId")
    void deleteByProductId(Long productId);
}
