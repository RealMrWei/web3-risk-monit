package com.web3.risk_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web3.risk_service.dto.ChatRequest;
import com.web3.risk_service.dto.ChatResponse;
import com.web3.risk_service.feign.AiAgentFeignClient;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskService riskService;

    @RequestMapping("/check")
    public String checkTransaction(@RequestBody TransactionRequest request) {
        return riskService.checkRisk(request);
    }

    @PostMapping("/check-with-ai")
    public String checkWithAi(@RequestBody String transactionInfo) {

        return riskService.checkWithAi(transactionInfo);
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
