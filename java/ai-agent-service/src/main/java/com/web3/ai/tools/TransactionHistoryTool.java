package com.web3.ai.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.web3.ai.dto.AccountDTO;
import com.web3.ai.dto.TxDTO;
import com.web3.ai.feign.AccountFeignClient;
import com.web3.ai.feign.TransactionFeignClient;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import io.swagger.v3.oas.annotations.media.Schema;

@Component
@RequiredArgsConstructor
public class TransactionHistoryTool {
    private final TransactionFeignClient transactionFeignClient;

    public static class TxHistoryRequest {
        @NotBlank
        @ToolParam(description = "用户ID")
        private String userId;

        @ToolParam(description = "查询条数，默认10")
        private Integer limit = 10;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }
    }

    @Tool(description = "查询用户历史交易记录")
    public List<Map<String, Object>> getTransactionHistory(TxHistoryRequest request) {
        System.out.println("getTransactionHistory: " + request);
        List<TxDTO> dtoList = transactionFeignClient.getTransactionHistory(
                request.getUserId(),
                request.getLimit());
        System.out.println("dtoList: " + dtoList);
        return dtoList.stream()
                .map(tx -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("txId", tx.getTxId());
                    map.put("amount", tx.getAmount());
                    map.put("type", tx.getType());
                    map.put("time", tx.getCreateTime());
                    return map;
                })
                .collect(Collectors.toList());
    }

}
