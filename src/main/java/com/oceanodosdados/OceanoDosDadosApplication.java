package com.oceanodosdados;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import com.oceanodosdados.config.Credentials;

@SpringBootApplication

@EnableConfigurationProperties(Credentials.class)
@EnableFeignClients

public class OceanoDosDadosApplication {
	public static void main(String[] args) {
		SpringApplication.run(OceanoDosDadosApplication.class, args);
	}
}
