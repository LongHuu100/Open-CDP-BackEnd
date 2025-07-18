package vn.flast.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import vn.flast.entities.warehouse.WareHouseItem;

import java.util.Date;
import java.util.List;

@Table(name = "warehouse_history")
@Entity
@Getter @Setter
public class WareHouseHistory {

    public static int CONFIRM_WAREHOUSE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "stock_id")
    private Integer stockId;

    @Column(name = "code")
    private String code;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "warehouser_id")
    private Integer warehouserId;

    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "info")
    private String info;

    @Column(name = "fee")
    private Long fee;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "discount")
    private String discount;

    @Column(name = "vat")
    private Integer vat;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "in_time")
    private Date inTime = new Date();

    @Column(name = "status")
    private Integer status;

    @Column(name = "status_confirm")
    private Integer statusConfirm = 0;

    @Transient
    private List<WareHouseItem> items;
}
