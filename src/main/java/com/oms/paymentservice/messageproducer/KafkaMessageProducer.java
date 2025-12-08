package com.oms.paymentservice.messageproducer;

import com.oms.paymentservice.dto.PaymentRequestDTO;
import com.oms.paymentservice.events.PaymentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.oms.paymentservice.configs.KafkaConfigs.FAILURE_PAYMENT_EVENT;
import static com.oms.paymentservice.configs.KafkaConfigs.SUCCESS_PAYMENT_EVENT;

@Component
public class KafkaMessageProducer {
    KafkaTemplate<String, Object> kafkaTemplate;
    public KafkaMessageProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void paymentSuccessEvent(PaymentRequestDTO paymentRequestDTO){
        PaymentEvent successEvent = new PaymentEvent(paymentRequestDTO);
        kafkaTemplate.send(SUCCESS_PAYMENT_EVENT,successEvent);
    }
    public void paymentFailureEvent(PaymentRequestDTO paymentRequestDTO){
        PaymentEvent failurePaymentEvent = new PaymentEvent(paymentRequestDTO);
        kafkaTemplate.send(FAILURE_PAYMENT_EVENT, failurePaymentEvent);
    }
}
