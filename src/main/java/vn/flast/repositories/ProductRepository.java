package vn.flast.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.flast.models.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}