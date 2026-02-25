package com.oceanodosdados.config;
import org.springframework.validation.annotation.Validated;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Validated
@ConfigurationProperties("app.pix")
public record Credentials(
    String clientId,
    String clientSecret,
	String certificate,
	boolean sandbox,
	boolean debug
) {

}
