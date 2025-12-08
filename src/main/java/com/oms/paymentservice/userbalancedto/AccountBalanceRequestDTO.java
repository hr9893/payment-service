package com.oms.paymentservice.userbalancedto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceRequestDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private Long accountNumber;
    private double availableBalance;
}
