package ma.microtech.smartshop.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.payment.PaymentRequestDTO;
import ma.microtech.smartshop.dto.payment.PaymentResponseDTO;
import ma.microtech.smartshop.entity.Client;
import ma.microtech.smartshop.entity.Order;
import ma.microtech.smartshop.entity.Paiement;
import ma.microtech.smartshop.enums.CustomerTier;
import ma.microtech.smartshop.enums.OrderStatus;
import ma.microtech.smartshop.enums.PaymentStatus;
import ma.microtech.smartshop.enums.PaymentType;
import ma.microtech.smartshop.exception.BusinessException;
import ma.microtech.smartshop.exception.NotFoundException;
import ma.microtech.smartshop.mapper.PaymentMapper;
import ma.microtech.smartshop.repository.ClientRepository;
import ma.microtech.smartshop.repository.OrderRepository;
import ma.microtech.smartshop.repository.PaiementRepository;
import ma.microtech.smartshop.service.interfaces.OrderService;
import ma.microtech.smartshop.service.interfaces.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal CASH_LIMIT = BigDecimal.valueOf(20000);
    private static final BigDecimal SILVER_THRESHOLD = BigDecimal.valueOf(1000);
    private static final BigDecimal GOLD_THRESHOLD = BigDecimal.valueOf(5000);
    private static final BigDecimal PLATINUM_THRESHOLD = BigDecimal.valueOf(15000);

    private final PaiementRepository paiementRepository;
    private final PaymentMapper paymentMapper;
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public PaymentResponseDTO addPayment(Long orderId, PaymentRequestDTO dto) {
        Order order = findOrderById(orderId);
        validateOrderStatus(order);

        BigDecimal availableAmount = calculateAvailableAmount(order);
        validatePaymentAmount(dto, availableAmount);

        Paiement paiement = createPaiement(order, dto);
        processPayment(order, paiement, dto);

        paiement = paiementRepository.save(paiement);
        orderRepository.save(order);

        checkAndConfirmOrder(order);

        return paymentMapper.toResponseDTO(paiement);
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order Not Found"));
    }

    private void validateOrderStatus(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Impossible to add paiement to PENDING orders");
        }
    }

    private BigDecimal calculateAvailableAmount(Order order) {
        BigDecimal totalTTC = getValueOrZero(order.getTotalTTC());
        BigDecimal montantRestant = getValueOrZero(order.getMontantRestant());
        BigDecimal montantReserve = getValueOrZero(order.getMontantReserve());

        BigDecimal montantDejaPaye = totalTTC.subtract(montantRestant);
        BigDecimal totalCouvert = montantDejaPaye.add(montantReserve);

        return totalTTC.subtract(totalCouvert);
    }

    private void validatePaymentAmount(PaymentRequestDTO dto, BigDecimal availableAmount) {
        if (dto.montant().compareTo(availableAmount) > 0) {
            throw new BusinessException("The amount exceeds the available balance : " + availableAmount + " DH");
        }

        if (dto.type() == PaymentType.ESPECES && dto.montant().compareTo(CASH_LIMIT) > 0) {
            throw new BusinessException("Cash payments are limited to 20,000 MAD");
        }
    }

    private Paiement createPaiement(Order order, PaymentRequestDTO dto) {
        String numero = "PAY-" + LocalDateTime.now();

        return Paiement.builder()
                .order(order)
                .numeroPaiement(numero)
                .montant(dto.montant())
                .type(dto.type())
                .build();
    }

    private void processPayment(Order order, Paiement paiement, PaymentRequestDTO dto) {
        if (isAutoEncaissement(dto.type())) {
            processEncaissementImmediat(order, paiement, dto.montant());
        } else {
            processEncaissementDiffere(order, paiement, dto.montant());
        }
    }

    private boolean isAutoEncaissement(PaymentType type) {
        return type == PaymentType.ESPECES || type == PaymentType.VIREMENT;
    }

    private void processEncaissementImmediat(Order order, Paiement paiement, BigDecimal montant) {
        paiement.setStatus(PaymentStatus.ENCAISSE);
        paiement.setDateEncaissement(LocalDate.now());

        BigDecimal montantRestant = getValueOrZero(order.getMontantRestant());
        order.setMontantRestant(montantRestant.subtract(montant));

        updateClientSpentAndTier(order.getClient(), montant);
    }

    private void processEncaissementDiffere(Order order, Paiement paiement, BigDecimal montant) {
        paiement.setStatus(PaymentStatus.EN_ATTENTE);

        BigDecimal montantReserve = getValueOrZero(order.getMontantReserve());
        order.setMontantReserve(montantReserve.add(montant));
    }

    private void checkAndConfirmOrder(Order order) {
        if (isOrderFullyPaid(order)) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }
    }

    private boolean isOrderFullyPaid(Order order) {
        return order.getMontantRestant() != null
                && order.getMontantRestant().compareTo(BigDecimal.ZERO) <= 0;
    }

    private void updateClientSpentAndTier(Client client, BigDecimal paidAmount) {
        BigDecimal currentSpent = getValueOrZero(client.getTotalSpent());
        client.setTotalSpent(currentSpent.add(paidAmount));

        upgradeClientTier(client);
        clientRepository.save(client);
    }

    private void upgradeClientTier(Client client) {
        CustomerTier newTier = determineCustomerTier(client);

        if (client.getTier() != newTier) {
            client.setTier(newTier);
        }
    }

    private CustomerTier determineCustomerTier(Client client) {
        BigDecimal totalSpent = getValueOrZero(client.getTotalSpent());
        Integer orderCount = client.getTotalOrders() != null ? client.getTotalOrders() : 0;

        if (orderCount >= 20 || totalSpent.compareTo(PLATINUM_THRESHOLD) >= 0) {
            return CustomerTier.PLATINUM;
        }
        if (orderCount >= 10 || totalSpent.compareTo(GOLD_THRESHOLD) >= 0) {
            return CustomerTier.GOLD;
        }
        if (orderCount >= 3 || totalSpent.compareTo(SILVER_THRESHOLD) >= 0) {
            return CustomerTier.SILVER;
        }

        return CustomerTier.BASIC;
    }

    private BigDecimal getValueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, PaymentStatus newStatus, LocalDate dateEncaissement){
        Paiement paiement = paiementRepository.findById(paymentId)
                .orElseThrow(()-> new NotFoundException("Paiement Not Found"));

        Order order = paiement.getOrder();

        if(paiement.getStatus() != PaymentStatus.EN_ATTENTE){
            throw new BusinessException("Only payments with status PENDING can be modified");
        }

        if(order.getStatus() != OrderStatus.PENDING){
            throw new BusinessException("Cannot modify a payment for an order that is not PENDING");
        }

        BigDecimal montant = paiement.getMontant();
        BigDecimal currentReserve = getValueOrZero(order.getMontantReserve());
        BigDecimal currentRestant = getValueOrZero(order.getMontantRestant());

        if(newStatus == PaymentStatus.ENCAISSE){
            order.setMontantReserve(currentReserve.subtract(montant));
            order.setMontantRestant(currentRestant.subtract(montant));

            updateClientSpentAndTier(order.getClient(), montant);

            paiement.setStatus(PaymentStatus.ENCAISSE);
            paiement.setDateEncaissement(dateEncaissement != null ? dateEncaissement : LocalDate.now());
        } else if (newStatus == PaymentStatus.REJETE) {
            order.setMontantReserve(currentReserve.subtract(montant));
            paiement.setStatus(PaymentStatus.REJETE);
        } else {
            throw new BusinessException("Invalid status: only PAID or REJECTED is allowed here");
        }

        paiement = paiementRepository.save(paiement);
        orderRepository.save(order);

        checkAndConfirmOrder(order);

        return paymentMapper.toResponseDTO(paiement);
    }
}