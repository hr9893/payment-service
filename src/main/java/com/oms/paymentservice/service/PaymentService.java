package com.oms.paymentservice.service;

import com.oms.paymentservice.customexception.PaymentCustomException;
import com.oms.paymentservice.dto.PaymentRequestDTO;
import com.oms.paymentservice.dto.FailureReason;
import com.oms.paymentservice.dto.PaymentStatus;
import com.oms.paymentservice.entity.Payment;
import com.oms.paymentservice.entity.UserAccountBalance;
import com.oms.paymentservice.events.PaymentEvent;
import com.oms.paymentservice.messageproducer.KafkaMessageProducer;
import com.oms.paymentservice.repository.AccountBalanceRepository;
import com.oms.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PaymentService  {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    AccountBalanceRepository accountBalanceRepository;
    @Autowired
    KafkaMessageProducer kafkaMessageProducer;

    public void handlePaymentEvent(PaymentEvent event) {
        final String methodName = "handlePaymentEvent";
        logger.info(methodName, "Entry");
        try {
            UserAccountBalance getUserBalanceResponse = accountBalanceRepository.getUserBalanceByUserId(event.getPaymentRequest().getUserId());

            if (getUserBalanceResponse.getAvailableBalance() >= event.getPaymentRequest().getOrderTotal()) {
                capturePaymentBasedOnEvent(event, true);
                preparePaymentEvent(event, true);

            } else {
                capturePaymentBasedOnEvent(event, false);
                preparePaymentEvent(event, false);
            }
        } catch (Exception e) {
            logger.error("Error handling payment event for orderId {}", event.getPaymentRequest().getOrderId(), e);
            throw new PaymentCustomException("Failed to process payment", e);
        }
        logger.info(methodName, "Exit");
    }

    public String getLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        return formattedDateTime;
    }

    public void preparePaymentEvent(PaymentEvent event, boolean isSuccessEvent) {
        final String methodName = "paymentSuccessEvent";
        logger.info(methodName, "{} Entry");

        PaymentRequestDTO paymentEvent = new PaymentRequestDTO();

        if (isSuccessEvent) {
            paymentEvent.setPaymentStatus(PaymentStatus.COMPLETED);
        } else {
            paymentEvent.setPaymentStatus(PaymentStatus.FAILED);
        }
        paymentEvent.setUserId(event.getPaymentRequest().getUserId());
        paymentEvent.setOrderId(event.getPaymentRequest().getOrderId());
        paymentEvent.setOrderTotal(event.getPaymentRequest().getOrderTotal());
        paymentEvent.setTransactionId(event.getPaymentRequest().getTransactionId());
        paymentEvent.setEventTimestamp(getLocalDateTime());

        kafkaMessageProducer.publishPaymentEvent(paymentEvent);
        logger.info(methodName, "{} Exit");
    }

    @Transactional
    private void capturePaymentBasedOnEvent(PaymentEvent event, boolean isSuccessPaymentEvent) {
        final String methodName = "updatePaymentBasedOnEvent";
        logger.info(methodName, "{} Entry");

        Payment updatedPayment = new Payment();
        UserAccountBalance getUserBalanceResponse = accountBalanceRepository.findById(event.getPaymentRequest().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (isSuccessPaymentEvent) {
            updatedPayment.setPaymentStatus(PaymentStatus.COMPLETED);
            getUserBalanceResponse.setAvailableBalance(getUserBalanceResponse.getAvailableBalance() - event.getPaymentRequest().getOrderTotal());
            accountBalanceRepository.save(getUserBalanceResponse);
        } else {
            updatedPayment.setPaymentStatus(PaymentStatus.FAILED);
            updatedPayment.setFailureReason(FailureReason.INSUFFICIENT_FUNDS);
        }
        updatedPayment.setOrderId(event.getPaymentRequest().getOrderId());
        updatedPayment.setUserId(event.getPaymentRequest().getUserId());
        updatedPayment.setPaymentTransactionId(event.getPaymentRequest().getTransactionId());
        updatedPayment.setOrderTotal(event.getPaymentRequest().getOrderTotal());
        updatedPayment.setCreatedTimestamp(event.getPaymentRequest().getEventTimestamp());

        paymentRepository.save(updatedPayment);

        logger.info(methodName, "{} Exit");

    }
}
