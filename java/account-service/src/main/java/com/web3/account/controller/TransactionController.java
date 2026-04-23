package com.web3.account.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web3.account.dto.TxDTO;

@RestController
@RequestMapping("/tx")
public class TransactionController {

    @GetMapping("/history/{userId}")
    public List<TxDTO> getTransactionHistory(@PathVariable String userId, @RequestParam Integer limit) {
        List<TxDTO> response = new ArrayList<>();
        System.out.println("Received request for transaction history of userId: " + userId + " with limit: " + limit);
        if ("1".equals(userId)) {
            response.add(new TxDTO("txId1", new BigDecimal("100"), "deposit", LocalDateTime.now()));
            response.add(new TxDTO("txId2", new BigDecimal("200"), "deposit", LocalDateTime.now()));
        } else if ("2".equals(userId)) {
            response.add(new TxDTO("txId3", new BigDecimal("50"), "withdrawal", LocalDateTime.now()));
        } else if ("3".equals(userId)) {
            response.add(new TxDTO("txId4", new BigDecimal("300"), "deposit", LocalDateTime.now()));
            response.add(new TxDTO("txId5", new BigDecimal("150"), "withdrawal", LocalDateTime.now()));
        }
        return response;
    }

}
