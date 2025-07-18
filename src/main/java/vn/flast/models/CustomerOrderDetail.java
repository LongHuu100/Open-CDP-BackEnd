package vn.flast.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.flast.utils.NumberUtils;
import java.util.Date;

@Table(name = "customer_order_detail")
@Entity
@Getter @Setter
public class CustomerOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "customer_order_id", nullable = false)
    private Long customerOrderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "warranty_period")
    private String warrantyPeriod;

    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "sku_info")
    private String skuInfo;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private Integer quantity = 0;

    @Column(name = "price_off")
    private Double priceOff;

    @Column(name = "total")
    private Double total;

    @Column(name = "day_quote")
    private String dayQuote;

    @Column(name = "customer_note")
    private String note;

    @Column(name = "status")
    private Integer status;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private Date updatedAt;

    @JsonBackReference(value = "details")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_order_id",referencedColumnName = "id", insertable=false, updatable=false)
    private CustomerOrder customerOrder;

    @PrePersist
    public void beforeSave() {
        if(NumberUtils.isNull(status)) {
            status = 0;
        }
    }
}
