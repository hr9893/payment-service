package com.oms.paymentservice.customexception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentCustomException extends RuntimeException{
    public PaymentCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
