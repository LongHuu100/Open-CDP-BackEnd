package vn.flast.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import vn.flast.entities.SkuAttributed;
import java.util.List;

@Table(name = "product_skus")
@Entity
@Getter @Setter
public class ProductSkus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "del")
    private Integer del = 0;

    @Transient
    private List<ProductSkusPrice> listPriceRange;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<SkuAttributed> sku; /* Sử dụng khi tạo Sản phẩm, Fetch thì không cần */

    @Transient
    private List<ProductSkusDetails> skuDetail;
}
