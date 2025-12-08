package com.oms.paymentservice.controller;

import com.oms.paymentservice.entity.UserAccountBalance;
import com.oms.paymentservice.service.AccountBalanceService;
import com.oms.paymentservice.userbalancedto.AccountBalanceRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/balance")
public class UserAccountBalanceController {
    @Autowired
    AccountBalanceService accountBalanceService;

    @PostMapping("/update")
    public UserAccountBalance updateUserBalance(@RequestBody AccountBalanceRequestDTO requestDTO){
        UserAccountBalance userAccountBalance = accountBalanceService.updateAccountDetails(requestDTO);

        return userAccountBalance;
    }

    @GetMapping("/getUserBalance/{userId}")
    public UserAccountBalance getUserBalance(@PathVariable String userId){
        UserAccountBalance  getAccountBalance = accountBalanceService.getUserBalanceByUserId(userId);

        return  getAccountBalance;
    }
}
