package com.web3.risk_service;

import org.springframework.stereotype.Service;

@Service
public class RiskService {
    // Large transaction threshold: 2 ETH
    private static final double LAGRE_AMOUNT = 2.0;

    /**
     * check transaction risk
     * 
     * @param request transaction request
     * @return risk result
     */
    public String checkRisk(TransactionRequest request) {
        System.out.println("======================");
        System.out.println("received on-chain transaction:");
        System.out.println("user address: " + request.getUserAddress());
        System.out.println("tx type: " + request.getTxType());
        System.out.println("amount: " + request.getAmount() + "ETH");
        if (request.getAmount() > LAGRE_AMOUNT) {
            System.out.println("large transaction detected!");
            return "large transaction detected!";
        } else {
            System.out.println("transaction passed!");
            return "transaction passed!";
        }
    }

}
