package com.web3.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AiAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiAgentApplication.class, args);
    }
}
