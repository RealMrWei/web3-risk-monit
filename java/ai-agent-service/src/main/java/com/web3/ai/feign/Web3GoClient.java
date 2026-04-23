package com.web3.ai.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.web3.ai.dto.EthDTO;

@FeignClient(name = "web3go-service", url = "${feign.web3go-service.url}")
public interface Web3GoClient {
    @GetMapping("/eth/balance/{address}")
    EthDTO getEthBalance(@PathVariable("address") String address);
}