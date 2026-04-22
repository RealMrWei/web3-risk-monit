package com.web3.ai.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import com.web3.ai.service.AiAgentService;

@Service
public class OpenAiAgentServiceImpl implements AiAgentService {

    private final ChatClient chatClient;

    // 通过构造函数注入 ChatClient.Builder，然后构建 ChatClient
    public OpenAiAgentServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String chat(String message) {
        // 使用 Spring AI 的 ChatClient 调用真实的 AI 模型
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @Override
    public String chattorisk(String message) {
        // 使用 Spring AI 的 ChatClient 调用真实的 AI 模型并启用工具
        // 提取内容并返回
        return chatClient.prompt()
                .user(message)
                .tools("checkTransactionRisk") // 启用工具
                .call()
                .content();

    }
}
