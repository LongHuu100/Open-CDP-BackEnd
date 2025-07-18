package vn.flast.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "data_media")
@Entity
@Getter @Setter
@NoArgsConstructor
public class DataMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "data_id")
    private Long dataId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "file")
    private String file;

    public DataMedia(
        @NotNull(message = "dataId is can't be null") Long dataId,
        @NotNull(message = "sessionId is can't be null") Long sessionId,
        @NotNull(message = "file is can't be null") String file
    ) {
        this.dataId = dataId;
        this.sessionId = sessionId;
        this.file = file;
    }
}
