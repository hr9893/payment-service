package com.oms.paymentservice.messagelistener;

import com.oms.paymentservice.events.PaymentEvent;
import com.oms.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.oms.paymentservice.configs.KafkaConfigs.*;

@Component
@Slf4j
public class KafkaMessageListener {
    @Autowired
    PaymentService paymentService;
    @KafkaListener(topics = IB_PAYMENT_EVENT, groupId = IB_PAYMENT_EVENT_GROUP)
    public void paymentEventListener(PaymentEvent event){
        log.info("Listening to Payment Event");
        paymentService.handlePaymentEvent(event);
    }
}
