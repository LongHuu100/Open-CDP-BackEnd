package vn.flast.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Collection;
import java.util.Date;

@Table(name = "customer_personal")
@Entity
@Getter @Setter
public class CustomerPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_card")
    private String idCard;

    @Column(name = "sale_id")
    private Integer saleId;

    @Column(name = "gender")
    private String gender;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "level")
    private Long level;

    @Column(name = "facebook_id")
    private String facebookId;

    @Column(name = "name")
    private String name;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "ward_id")
    private Long wardId;

    @Column(name = "address")
    private String address;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "email")
    private String email;

    @Column(name = "is_trust_email")
    private Long isTrustEmail;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "password")
    private String password;

    @Column(name = "token_confirm")
    private String tokenConfirm;

    @Column(name = "status")
    private Long status;

    @Column(name = "num_of_order")
    private Integer numOfOrder = 0;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "diem_danh_gia")
    private Integer diemDanhGia;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonManagedReference(value = "customerAddress")
    @OneToMany(mappedBy = "customerPersonal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<CustomerAddress> customerAddress;

    @PrePersist
    private void beforeSave() {
        if(StringUtils.isEmpty(gender)) {
            gender = "other";
        }
    }
}
