package com.web3.ai.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web3.ai.dto.ChatRequest;
import com.web3.ai.dto.ChatResponse;
import com.web3.ai.service.AiAgentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai/agent")
@RequiredArgsConstructor
public class AiAgentController {

    private final AiAgentService aiAgentService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatRequest request) {
        Flux<String> answer = aiAgentService.chat(request.getMessage());
        return answer;
    }

    @PostMapping("/chatwithnotool")
    public ChatResponse chatwithnotool(@RequestBody ChatRequest request) {
        String answer = aiAgentService.chatwithnotool(request.getMessage());
        return new ChatResponse(answer);
    }

    @PostMapping("/chattorisk")
    public ChatResponse chattorisk(@RequestBody ChatRequest request) {
        String answer = aiAgentService.chattorisk(request.getMessage());
        return new ChatResponse(answer);
    }
}
