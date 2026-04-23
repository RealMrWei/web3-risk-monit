package com.web3.ai.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import com.web3.ai.service.AiAgentService;
import com.web3.ai.tools.AccountBalanceTool;
import com.web3.ai.tools.EthBalanceTool;
import com.web3.ai.tools.TransactionHistoryTool;

import reactor.core.publisher.Flux;

@Service
public class OpenAiAgentServiceImpl implements AiAgentService {

    private final ChatClient chatClient;
    private final AccountBalanceTool accountBalanceTool;
    private final TransactionHistoryTool transactionHistoryTool;
    private final EthBalanceTool ethBalanceTool;

    public OpenAiAgentServiceImpl(
            ChatClient.Builder chatClientBuilder,
            AccountBalanceTool accountBalanceTool,
            TransactionHistoryTool transactionHistoryTool,
            EthBalanceTool ethBalanceTool) {
        this.chatClient = chatClientBuilder.build();
        this.accountBalanceTool = accountBalanceTool;
        this.transactionHistoryTool = transactionHistoryTool;
        this.ethBalanceTool = ethBalanceTool;
    }

    @Override
    public Flux<String> chat(String message) {
        // 使用 Spring AI 的 ChatClient 调用真实的 AI 模型
        System.out.println("Received message: " + message);
        return chatClient.prompt()
                .user(message)
                .tools(accountBalanceTool, transactionHistoryTool, ethBalanceTool) // 启用工具
                .stream()
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

    @Override
    public String chatwithnotool(String message) {
        // TODO Auto-generated method stub
        return chatClient.prompt()
                .user(message)
                // 不启用工具
                .call()
                .content();
    }
}
