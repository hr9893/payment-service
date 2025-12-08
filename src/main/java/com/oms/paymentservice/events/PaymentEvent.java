package com.oms.paymentservice.events;

import com.oms.paymentservice.dto.PaymentRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentEvent {
    private PaymentRequestDTO paymentRequest;
}
