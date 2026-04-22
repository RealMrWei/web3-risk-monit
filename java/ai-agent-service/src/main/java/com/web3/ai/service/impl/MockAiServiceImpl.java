package com.web3.ai.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.web3.ai.client.LlmClient;
import com.web3.ai.service.AiAgentService;

// 注释掉 @Service，让真实实现生效
// @Service
public class MockAiServiceImpl implements AiAgentService, LlmClient {

    @Override
    public String chat(String message) {
        // TODO Auto-generated method stub
        return "【AI-Agent】已接收：" + message
                + "\n当前模式：Mock 模拟调用"
                + "\n风控助手已就绪";
    }

    @Override
    public String chattorisk(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chattorisk'");
    }

}
