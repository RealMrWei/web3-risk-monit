package com.web3.ai.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthDTO {
    private String address;
    private BigDecimal balance;
    private BigDecimal usdtValue;
}
