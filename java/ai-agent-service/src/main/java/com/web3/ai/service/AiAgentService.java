package com.web3.ai.service;

import reactor.core.publisher.Flux;

public interface AiAgentService {
    Flux<String> chat(String message);

    String chattorisk(String message);

    String chatwithnotool(String message);
}
