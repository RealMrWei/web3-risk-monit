package com.web3.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TxDTO {
    private String txId;
    private BigDecimal amount;
    private String type;
    private LocalDateTime createTime;
}
