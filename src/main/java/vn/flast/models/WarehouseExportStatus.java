package vn.flast.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "warehouse_export_status")
@Entity
@Getter @Setter
public class WarehouseExportStatus {

    public static int TYPE_CONFIRM = 1;
    public static int TYPE_NOT_CONFIRM = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name= "type")
    private Integer type;
}
