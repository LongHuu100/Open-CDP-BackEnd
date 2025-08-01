package vn.flast.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.flast.models.DataMedia;
import java.util.List;

public interface DataMediaRepository extends JpaRepository<DataMedia, Long> {
    @Query("FROM DataMedia d WHERE d.dataId = :dataId")
    List<DataMedia> findByDataId (Long dataId);
}
