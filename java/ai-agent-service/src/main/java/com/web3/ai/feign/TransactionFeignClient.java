package com.web3.ai.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.web3.ai.dto.TxDTO;

@FeignClient(name = "transaction-service", url = "${feign.transaction-service.url}")
public interface TransactionFeignClient {
    @GetMapping("/tx/history/{userId}")
    List<TxDTO> getTransactionHistory(
            @PathVariable("userId") String userId,
            @RequestParam("limit") Integer limit);
}