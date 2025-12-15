package com.oms.paymentservice.entity;

import com.oms.paymentservice.dto.FailureReason;
import com.oms.paymentservice.dto.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(name = "PAYMENT_METHOD")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Column(name = "ORDER_ID")
    @Id
    private String orderId;
    @Column(name = "PAYMENT_TRANSACTION_ID")
    private Long paymentTransactionId;
    @Column(name = "ORDER_TOTAL")
    private double orderTotal;
    @Column(name = "UNIT_PRICE")
    private double unitPrince;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "CREATED_TIMESTAMP")
    private String createdTimestamp;
    @Version
    @Column(name = "VERSION")
    private Long version;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private FailureReason failureReason;
}
