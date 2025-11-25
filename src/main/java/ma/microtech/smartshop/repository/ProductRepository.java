package ma.microtech.smartshop.repository;

import ma.microtech.smartshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByDeletedFalse();

//    @Query("select COUNT(oi) > 0 from OrderItem oi where oi.product.id = :productId")
//    boolean isUsedInAnyOrder(Long productId);
    default boolean isUsedInAnyOrder(Long productId) {
        return false;
    }
}
