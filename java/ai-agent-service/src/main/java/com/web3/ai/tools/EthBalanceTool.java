package com.web3.ai.tools;

import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.web3.ai.dto.EthDTO;
import com.web3.ai.feign.Web3GoClient;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EthBalanceTool {
    private final Web3GoClient web3GoClient;

    public static class EthBalanceRequest {
        @NotBlank
        @ToolParam(description = "以太坊钱包地址 0x...")
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    @Tool(description = "查询以太坊地址的ETH余额")
    public Map<String, Object> getEthBalance(EthBalanceRequest request) {
        System.out.println("getEthBalance: " + request.getAddress());
        EthDTO dto = web3GoClient.getEthBalance(request.getAddress());
        System.out.println("getEthBalance: " + dto);
        return Map.of(
                "address", request.getAddress(),
                "ethBalance", dto.getBalance(),
                "usdtValue", dto.getUsdtValue());
    }

}
