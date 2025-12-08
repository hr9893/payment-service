package com.oms.paymentservice.repository;

import com.oms.paymentservice.entity.UserAccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBalanceRepository extends JpaRepository<UserAccountBalance,String> {
    public UserAccountBalance getUserBalanceByUserId(String userId);
}
