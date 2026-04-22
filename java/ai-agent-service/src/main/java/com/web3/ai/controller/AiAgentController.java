package com.web3.ai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web3.ai.dto.ChatRequest;
import com.web3.ai.dto.ChatResponse;
import com.web3.ai.service.AiAgentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai/agent")
@RequiredArgsConstructor
public class AiAgentController {

    private final AiAgentService aiAgentService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String answer = aiAgentService.chat(request.getMessage());
        return new ChatResponse(answer);
    }

    @PostMapping("/chattorisk")
    public ChatResponse chattorisk(@RequestBody ChatRequest request) {
        String answer = aiAgentService.chattorisk(request.getMessage());
        return new ChatResponse(answer);
    }
}
