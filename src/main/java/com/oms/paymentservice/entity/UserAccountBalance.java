package com.oms.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "USER_ACCOUNT_BALANCE")
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountBalance {
    @Id
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastname;
    @Column(name = "ACCOUNT_NUMBER")
    private Long accountNumber;
    @Column(name = "AVAILABLE_BALANCE")
    private double availableBalance;
    @Version
    @Column(name = "VERSION")
    private Long version;
}
