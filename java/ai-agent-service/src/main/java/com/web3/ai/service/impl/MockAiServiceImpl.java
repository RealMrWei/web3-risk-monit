package com.web3.ai.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.web3.ai.service.AiAgentService;

import reactor.core.publisher.Flux;

// 注释掉 @Service，让真实实现生效
// @Service
public class MockAiServiceImpl implements AiAgentService {

    @Override
    public Flux<String> chat(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chat'");
    }

    @Override
    public String chattorisk(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chattorisk'");
    }

    @Override
    public String chatwithnotool(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chatwithnotool'");
    }

}
