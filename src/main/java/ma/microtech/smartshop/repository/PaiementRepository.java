package ma.microtech.smartshop.repository;

import ma.microtech.smartshop.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    List<Paiement> findByOrderIdOrderByDatePaiementDesc(Long orderId);
}
