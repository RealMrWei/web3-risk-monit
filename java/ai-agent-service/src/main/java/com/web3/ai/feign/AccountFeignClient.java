package com.web3.ai.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.web3.ai.dto.AccountDTO;

@FeignClient(name = "account-service", url = "${feign.account-service.url}")
public interface AccountFeignClient {
    @GetMapping("/account/balance/{userId}")
    public AccountDTO getAccountBalance(@PathVariable("userId") String userId);
}