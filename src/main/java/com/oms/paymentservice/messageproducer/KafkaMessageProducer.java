package com.oms.paymentservice.messageproducer;

import com.oms.paymentservice.dto.PaymentRequestDTO;
import com.oms.paymentservice.dto.PaymentStatus;
import com.oms.paymentservice.events.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.oms.paymentservice.configs.KafkaConfigs.FAILURE_PAYMENT_EVENT;
import static com.oms.paymentservice.configs.KafkaConfigs.SUCCESS_PAYMENT_EVENT;

@Component
@Slf4j
public class KafkaMessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageProducer.class);
    KafkaTemplate<String, Object> kafkaTemplate;
    public KafkaMessageProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentEvent(PaymentRequestDTO paymentRequest) {
        final String methodName = "publishPaymentEvent";
        logger.info("Entry {}", methodName);
        PaymentEvent PaymentEvent = new PaymentEvent(paymentRequest);
        logger.info("Payment Event for Order Service : {} ", paymentRequest.toString());
        if (PaymentEvent.getPaymentRequest().getPaymentStatus().equals(PaymentStatus.COMPLETED)) {
            kafkaTemplate.send(SUCCESS_PAYMENT_EVENT, PaymentEvent);
        } else
            kafkaTemplate.send(FAILURE_PAYMENT_EVENT, PaymentEvent);
    }
}
