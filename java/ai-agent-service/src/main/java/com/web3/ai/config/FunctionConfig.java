package com.web3.ai.config;

import java.util.Map;
import java.util.function.Function;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FunctionConfig {
    // 工具名：checkTransactionRisk
    // 大模型会自动识别并调用这个函数来进行交易风险检测
    @Bean
    public FunctionCallback checkTransactionRisk() {
        Function<Map<String, Object>, Map<String, Object>> checkRiskFunc = params -> {
            String from = (String) params.get("from");
            String to = (String) params.get("to");
            Number amount = (Number) params.get("amount");
            System.out.println("链上交易风险检测开始,参数包括:from(" + from + "), to(" + to + "), amount(" + amount + ")");
            // 这里你可以调用风控服务的API来进行风险检测
            String riskLevel = "low";
            if (amount.doubleValue() > 1000) {
                riskLevel = "high";
            }
            return Map.of(
                    "code", 0,
                    "riskLevel", riskLevel,
                    "from", from,
                    "to", to,
                    "amount", amount,
                    "desc", "链上交易风险检测完成");
        };

        return FunctionCallback.builder()
                .function("checkTransactionRisk", checkRiskFunc)
                .description("检测链上交易的风险等级,参数包括:from(发送地址), to(接收地址), amount(交易金额)")
                .inputType(Map.class)
                .build();
    }
}
