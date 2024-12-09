package vn.flast.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.flast.models.CustomerOrderPayment;
import java.util.List;

public interface CustomerOrderPaymentRepository extends JpaRepository<CustomerOrderPayment, Long> {
    @Query("FROM CustomerOrderPayment p WHERE p.code =:code")
    List<CustomerOrderPayment> findCodes(String code);
}
