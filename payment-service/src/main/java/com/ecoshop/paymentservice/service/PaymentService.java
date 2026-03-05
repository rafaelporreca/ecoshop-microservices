package com.ecoshop.paymentservice.service;

import com.ecoshop.paymentservice.event.InventoryReservedEvent;
import com.ecoshop.paymentservice.event.PaymentFailedEvent;
import com.ecoshop.paymentservice.event.PaymentProcessedEvent;
import com.ecoshop.paymentservice.model.Payment;
import com.ecoshop.paymentservice.model.PaymentStatus;
import com.ecoshop.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "inventory-success-topic", groupId = "payment-group")
    @Transactional
    public void processPayment(InventoryReservedEvent event) {
        log.info("Iniciando cobrança para o pedido ID: {}", event.orderId());

        Payment payment = new Payment();
        payment.setOrderId(event.orderId());

        // Simulador de Gateway de Pagamento (Par = Aprova, Ímpar = Recusa)
        boolean isPaymentApproved = event.orderId() % 2 == 0;

        if (isPaymentApproved) {
            payment.setStatus(PaymentStatus.APPROVED);
            paymentRepository.save(payment);

            log.info("Pagamento APROVADO para o pedido {}.", event.orderId());
            kafkaTemplate.send("payment-success-topic", String.valueOf(event.orderId()),
                    new PaymentProcessedEvent(event.orderId()));
        } else {
            payment.setStatus(PaymentStatus.REJECTED);
            paymentRepository.save(payment);

            log.error("Pagamento RECUSADO (Sem limite) para o pedido {}.", event.orderId());
            kafkaTemplate.send("payment-failed-topic", String.valueOf(event.orderId()),
                    new PaymentFailedEvent(event.orderId(), "Cartão sem limite disponível"));
        }
    }
}
