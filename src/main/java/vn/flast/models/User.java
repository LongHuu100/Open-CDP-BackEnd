package vn.flast.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter @Setter
public class User {

    public static final String RULE_ADMIN = "ADMIN";
    public static final String RULE_PERCHARING = "PERCHARGING";
    public static final String RULE_KHO = "WAREHOUSE";
    public static final String RULE_MANAGER = "SALE_MANAGER";
    public static final String RULE_SALE_LEADER = "SALE_LEADER";
    public static final String RULE_SALE_MENBER = "SALE";
    public static final String RULE_PARTNER = "PARTNER";
    public static final String RULE_CSKH = "DBA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "sso_id", nullable = false)
    private String ssoId;

    @NotNull
    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Lob
    @Column(name = "firebase_token")
    private String fireBaseToken;

    @Column(name = "layout")
    private String layout;

    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "status")
    private Integer status;

    @Column(name = "avartar")
    private String avartar;

    @Column(name = "address")
    private String address;

    @Transient
    public List<Integer> permissions = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_link_profile",
        joinColumns = @JoinColumn(name = "user_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "user_profile_id", nullable = false)
    )
    private Set<UserProfile> userProfiles = new HashSet<>();


    @Override
    public User clone() {
        try {
            User uClone = (User) super.clone();
            /* super.clone() là deep Clone khi object gốc hoặc object clone thay đổi cũng không ảnh hưởng nhau
             * uClone.address = this.address.clone(); */
            uClone.userProfiles = new HashSet<>();
            uClone.password = "";
            return uClone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @JsonIgnore
    @Transient
    public boolean checkRule(String rule) {
        if(this.userProfiles.isEmpty()) {
            return false;
        }
        for(UserProfile userProfile : this.userProfiles) {
            if(userProfile.getType().contains(rule)) {
                return true;
            }
        }
        return false;
    }

    public boolean ruleSaleManager() {
        return this.checkRule(RULE_MANAGER);
    }

    public boolean ruleKho() {
        return this.checkRule(RULE_KHO);
    }

    public boolean ruleCskh() {
        return this.checkRule(RULE_CSKH);
    }
}
