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
                updateUserBalance(event, getUserBalanceResponse);
                capturePaymentBasedOnEvent(event, true);

                paymentSuccessEvent(event);
            } else {
                capturePaymentBasedOnEvent(event, false);

                paymentFailureEvent(event);
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

    public void paymentSuccessEvent(PaymentEvent event) {
        final String methodName = "paymentSuccessEvent";
        logger.info(methodName, "Entry");

        PaymentRequestDTO paymentEvent = new PaymentRequestDTO();

        paymentEvent.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentEvent.setOrderId(event.getPaymentRequest().getOrderId());
        paymentEvent.setUserId(event.getPaymentRequest().getUserId());
        paymentEvent.setTransactionId(event.getPaymentRequest().getTransactionId());
        paymentEvent.setEventTimestamp(getLocalDateTime());

        kafkaMessageProducer.paymentSuccessEvent(paymentEvent);

        logger.info(methodName, "Exit");
    }

    private void paymentFailureEvent(PaymentEvent event) {
        final String methodName = "paymentFailureEvent";
        logger.info(methodName, "Entry");

        PaymentRequestDTO paymentEvent = new PaymentRequestDTO();

        paymentEvent.setPaymentStatus(PaymentStatus.FAILED);
        paymentEvent.setUserId(event.getPaymentRequest().getUserId());
        paymentEvent.setOrderId(event.getPaymentRequest().getOrderId());
        paymentEvent.setEventTimestamp(getLocalDateTime());

        kafkaMessageProducer.paymentFailureEvent(paymentEvent);
        logger.info(methodName, "Exit");
    }

    @Transactional
    private void capturePaymentBasedOnEvent(PaymentEvent event, boolean isSuccessPaymentEvent) {
        final String methodName = "updatePaymentBasedOnEvent";
        logger.info(methodName, "Entry");

        Payment updatedPayment = new Payment();

        if (isSuccessPaymentEvent) {
            updatedPayment.setOrderId(event.getPaymentRequest().getOrderId());
            updatedPayment.setUserId(event.getPaymentRequest().getUserId());
            updatedPayment.setPaymentTransactionId(event.getPaymentRequest().getTransactionId());
            updatedPayment.setOrderTotal(event.getPaymentRequest().getOrderTotal());
            updatedPayment.setCreatedTimestamp(event.getPaymentRequest().getEventTimestamp());
            updatedPayment.setPaymentStatus(PaymentStatus.COMPLETED);
        } else {
            updatedPayment.setOrderId(event.getPaymentRequest().getOrderId());
            updatedPayment.setUserId(event.getPaymentRequest().getUserId());
            updatedPayment.setPaymentTransactionId(event.getPaymentRequest().getTransactionId());
            updatedPayment.setOrderTotal(event.getPaymentRequest().getOrderTotal());
            updatedPayment.setCreatedTimestamp(event.getPaymentRequest().getEventTimestamp());
            updatedPayment.setPaymentStatus(PaymentStatus.FAILED);
            updatedPayment.setFailureReason(FailureReason.INSUFFICIENT_FUNDS);
        }
        paymentRepository.save(updatedPayment);
        logger.info(methodName, "Exit");

    }

    @Transactional
    private void updateUserBalance(PaymentEvent event, UserAccountBalance userAccountBalance) {
        final String methodName = "updateUserBalance";
        logger.info(methodName, "Entry");

        UserAccountBalance updateUserBalance = accountBalanceRepository.findById(event.getPaymentRequest().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        updateUserBalance.setAvailableBalance(userAccountBalance.getAvailableBalance() - event.getPaymentRequest().getOrderTotal());

        accountBalanceRepository.save(updateUserBalance);
        logger.info(methodName, "Exit");
    }
}
