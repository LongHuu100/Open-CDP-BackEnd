package vn.flast.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Table(name = "data_media")
@Entity
@Getter
@Setter
public class DataOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "customer_mobile")
    private String customerMobile;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "sale_name")
    private Long saleName;

    @Column(name = "in_time")
    private Date inTime;
}