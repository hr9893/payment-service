package com.oms.paymentservice.service;

import com.oms.paymentservice.entity.UserAccountBalance;
import com.oms.paymentservice.repository.AccountBalanceRepository;
import com.oms.paymentservice.userbalancedto.AccountBalanceRequestDTO;
import com.oms.paymentservice.userbalancedto.AccountBalanceResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountBalanceService {
    @Autowired
    AccountBalanceRepository accountBalanceRepository;
    public UserAccountBalance updateAccountDetails(AccountBalanceRequestDTO accountBalanceRequest){
        UserAccountBalance updateUserBalance = new UserAccountBalance();

        updateUserBalance.setUserId(accountBalanceRequest.getUserId());
        updateUserBalance.setFirstName(accountBalanceRequest.getFirstName());
        updateUserBalance.setLastname(accountBalanceRequest.getLastName());
        updateUserBalance.setAccountNumber(accountBalanceRequest.getAccountNumber());
        updateUserBalance.setAvailableBalance(accountBalanceRequest.getAvailableBalance());

        accountBalanceRepository.save(updateUserBalance);
        //AccountBalanceResponseDTO accountBalanceResponse = getUserBalanceByUserId(accountBalanceRequest.getUserId());

        return updateUserBalance;
    }

    public UserAccountBalance getUserBalanceByUserId(String userId){

        UserAccountBalance accountBalance = accountBalanceRepository.getUserBalanceByUserId(userId);

        return accountBalance;
    }
}
