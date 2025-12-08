package com.oms.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentRequestDTO {
    private String userId;
    private String orderId;
    private long transactionId;
    private double orderTotal;
    private String eventTimestamp;
    private PaymentStatus paymentStatus;
}
