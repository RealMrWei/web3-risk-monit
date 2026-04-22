package com.web3.risk_service;

import org.springframework.stereotype.Service;

import com.web3.risk_service.dto.ChatRequest;
import com.web3.risk_service.dto.ChatResponse;
import com.web3.risk_service.feign.AiAgentFeignClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiskService {
    // Large transaction threshold: 2 ETH
    private static final double LAGRE_AMOUNT = 2.0;

    private final AiAgentFeignClient aiAgentFeignClient;

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

    @CircuitBreaker(name = "default", fallbackMethod = "checkWithAiFallback")
    public String checkWithAi(String transactionInfo) {
        // TODO Auto-generated method stub
        // 1. 你的核心风控逻辑（这里模拟）
        String riskResult = "交易风险检测：低风险";

        // 2. 调用 AI 增强分析
        ChatRequest request = new ChatRequest();
        request.setMessage("对以下交易做风控分析：" + transactionInfo);

        ChatResponse aiResponse = aiAgentFeignClient.chat(request);

        // 3. 合并返回
        return riskResult + "\nAI 分析：" + aiResponse.getAnswer();
    }

    // 降级方法（参数必须与原方法一致，可额外添加Exception）
    public String checkWithAiFallback(String transactionInfo, Exception e) {
        System.out.println("========================================");
        System.out.println("【熔断器触发】降级方法被调用！");
        System.out.println("原始请求: " + transactionInfo);
        System.out.println("异常类型: " + e.getClass().getName());
        System.out.println("异常信息: " + e.getMessage());
        System.out.println("========================================");
        return "交易风险检测：低风险\nAI 分析：AI 服务暂时不可用，请稍后重试（错误：" + e.getMessage() + "）";
    }

}
