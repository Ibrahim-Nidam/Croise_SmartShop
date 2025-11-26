package ma.microtech.smartshop.repository;

import ma.microtech.smartshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
