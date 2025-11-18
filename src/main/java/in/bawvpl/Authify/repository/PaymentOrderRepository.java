package in.bawvpl.Authify.repository;

import in.bawvpl.Authify.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByOrderId(String orderId);
    List<PaymentOrder> findByUserId(String userId);
}

