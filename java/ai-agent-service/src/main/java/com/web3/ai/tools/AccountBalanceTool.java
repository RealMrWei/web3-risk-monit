package com.web3.ai.tools;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.web3.ai.dto.AccountDTO;
import com.web3.ai.feign.AccountFeignClient;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.tool.annotation.Tool;
import io.swagger.v3.oas.annotations.media.Schema;

@Component
@RequiredArgsConstructor
public class AccountBalanceTool {

    private final AccountFeignClient accountFeignClient;

    public static class AccountBalanceRequest {
        @NotBlank
        @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private String userId;

        public AccountBalanceRequest() {
        }

        public AccountBalanceRequest(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    @Tool(name = "getAccountBalance", description = "查询平台账户余额")
    public Map<String, Object> getAccountBalance(AccountBalanceRequest req) {
        // 获取用户余额
        // 真实业务：你自己查DB/RPC
        String userId = req.getUserId();
        System.out.println("Fetching account balance for userId: " + userId);
        AccountDTO dto = accountFeignClient.getAccountBalance(userId);
        System.out.println("Received account balance: " + dto);
        return Map.of(
                "userId", userId,
                "balance", dto.getBalance(),
                "status", dto.getStatus());
    }

}
