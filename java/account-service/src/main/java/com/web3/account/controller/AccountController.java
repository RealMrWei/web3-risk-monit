package com.web3.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web3.account.dto.AccountDTO;

@RestController
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/balance/{userId}")
    public AccountDTO getAccountBalance(@PathVariable String userId) {
        System.out.println("accountcontroller查询用户ID " + userId + " 的账户余额");
        AccountDTO response = new AccountDTO();
        String bString = "1000.00";
        String staString = "active";
        if ("1".equals(userId)) {
            bString = "15800.50";
            staString = "正常";
        } else if ("2".equals(userId)) {
            bString = "0.00";
            staString = "冻结";
        } else if ("3".equals(userId)) {
            bString = "500.00";
            staString = "异常";
        }
        response.setUserId(userId);
        response.setBalance(bString);
        response.setStatus(staString);
        return response;
    }
}
