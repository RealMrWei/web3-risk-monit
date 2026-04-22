package com.web3.risk_service.feign;

import org.springframework.stereotype.Component;

import com.web3.risk_service.dto.ChatRequest;
import com.web3.risk_service.dto.ChatResponse;

@Component
public class AiAgentFallback implements AiAgentFeignClient {
    @Override
    public ChatResponse chat(ChatRequest request) {
        // Return a default response or handle the fallback logic here
        ChatResponse response = new ChatResponse();
        response.setAnswer("AI Agent Service is currently unavailable. Please try again later.");
        return response;
    }

}
