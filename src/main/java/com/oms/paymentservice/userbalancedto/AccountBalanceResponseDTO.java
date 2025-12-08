package com.oms.paymentservice.userbalancedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceResponseDTO {
    String userId;
    String firstName;
    String lastName;
    Long accountNumber;
    double availableBalance;
}
