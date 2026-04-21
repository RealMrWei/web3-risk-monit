package com.web3.risk_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk")
public class RiskController {
    @Autowired
    private RiskService riskService;

    @RequestMapping("/check")
    public String checkTransaction(@RequestBody TransactionRequest request) {
        return riskService.checkRisk(request);
    }

    /**
     * Health check API for testing service status
     * 
     * @return service running status
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "✅ Spring Boot Risk Service is running successfully!";
    }
}
