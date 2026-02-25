package com.oceanodosdados.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.oceanodosdados.config.HubDoDevFeignConfig;
import com.oceanodosdados.records.HubDoDevResponse;

@FeignClient(
    name = "hubDoDevClient",
    url = "${hdd.url}", 
    configuration = HubDoDevFeignConfig.class
)
public interface HubDoDevClient{
    @GetMapping("/cadastropf")
    HubDoDevResponse getCadastroPF(
        @RequestParam("cpf") String cpf,
        @RequestParam("token") String token
    );
}
