package vn.flast.repositories;

import vn.flast.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "FROM User u WHERE u.email=:email")
    User findByEmail(String email);

    @Query(value = "FROM User u WHERE u.ssoId = :ssoId")
    User findBySsoId(String ssoId);

    Boolean existsBySsoId(String ssoId);
    Boolean existsByEmail(String email);
}
