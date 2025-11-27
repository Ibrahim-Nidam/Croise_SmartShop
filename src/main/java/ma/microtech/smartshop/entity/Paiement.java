package ma.microtech.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.microtech.smartshop.enums.PaymentStatus;
import ma.microtech.smartshop.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "numero_paiement", unique = true, length = 50)
    private String numeroPaiement;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime datePaiement = LocalDateTime.now();

    @Column
    private LocalDate dateEncaissement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.EN_ATTENTE;

}
