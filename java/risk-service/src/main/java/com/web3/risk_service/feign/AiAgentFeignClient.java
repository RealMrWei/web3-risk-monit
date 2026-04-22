package com.web3.risk_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.web3.risk_service.dto.ChatRequest;
import com.web3.risk_service.dto.ChatResponse;

@FeignClient(name = "ai-agent-service", url = "${ai.agent.url}", fallback = AiAgentFallback.class)
public interface AiAgentFeignClient {
    @PostMapping("/ai/agent/chat")
    ChatResponse chat(@RequestBody ChatRequest request);
}
