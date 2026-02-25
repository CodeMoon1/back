package com.oceanodosdados.service;

import com.oceanodosdados.config.Credentials;
import com.oceanodosdados.repository.PixRepository;
import com.oceanodosdados.domain.PixCharge;
import com.oceanodosdados.enums.StatusEfi;
import com.oceanodosdados.records.apiPix.PixRequest;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Base64;

@Service
public class PixService {

    private static final Logger log = LoggerFactory.getLogger(PixService.class );

    @Value("${chave.pix}")
    private String keyPix;

    private final Credentials credentials;
    private final PixRepository pixRepository;
    private final RestTemplate restTemplate;

    public PixService(Credentials credentials, PixRepository pixRepository) {
        this.credentials = credentials;
        this.pixRepository = pixRepository;
        this.restTemplate = createIsolatedRestTemplate();
    }

    private RestTemplate createIsolatedRestTemplate() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(credentials.certificate())) {
                keyStore.load(fis, "".toCharArray());
            }

            SSLContext sslContext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, "".toCharArray())
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .build();

            HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom( )
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    .build();

            return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient ));
        } catch (Exception e) {
            log.error("Falha crítica ao inicializar o cliente HTTP seguro para Efí", e);
            throw new RuntimeException("Erro ao configurar SSL para Pix", e);
        }
    }

    public JSONObject criaPix(PixRequest pixRequest) {
        log.info("Iniciando criação de cobrança PIX para o CPF: {}", pixRequest.cpf());
        try {
            String accessToken = getAccessToken();

            JSONObject body = new JSONObject();
            body.put("calendario", new JSONObject().put("expiracao", 3600));
            body.put("devedor", new JSONObject()
                    .put("cpf", pixRequest.cpf())
                    .put("nome", pixRequest.nome()));
            body.put("valor", new JSONObject().put("original", "1.00"));
            body.put("chave", keyPix);
            body.put("solicitacaoPagador", "Serviço realizado");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            String baseUrl = credentials.sandbox() ? "https://pix-h.api.efipay.com.br" : "https://pix.api.efipay.com.br";
            String url = baseUrl + "/v2/cob";

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class );

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                processAndSave(jsonResponse);
                return jsonResponse;
            } else {
                throw new RuntimeException("Erro na API Efí: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Erro técnico ao processar PIX (Conflito RabbitMQ evitado)", e);
            throw new RuntimeException("Erro técnico PIX: " + e.getMessage(), e);
        }
    }

    private String getAccessToken() {
    try {
        String baseUrl = credentials.sandbox() ? "https://pix-h.api.efipay.com.br" : "https://pix.api.efipay.com.br";
        String url = baseUrl + "/oauth/token";

        HttpHeaders headers = new HttpHeaders( );
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Autenticação básica com ClientID e ClientSecret
        String auth = credentials.clientId() + ":" + credentials.clientSecret();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        // O corpo deve ser exatamente este para a Efí
        HttpEntity<String> entity = new HttpEntity<>("{\"grant_type\": \"client_credentials\"}", headers);

        log.info("Tentando obter token OAuth na URL: {}", url);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        JSONObject json = new JSONObject(response.getBody());
        return json.getString("access_token");

    } catch (org.springframework.web.client.HttpClientErrorException e) {
        // Log detalhado do erro retornado pela Efí (ex: 401 Unauthorized)
        log.error("Erro de autenticação da Efí (HTTP {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
        throw new RuntimeException("Falha na autenticação com Efí: Verifique ClientID/Secret");
    } catch (Exception e) {
        log.error("Erro inesperado ao obter token", e);
        throw new RuntimeException("Falha na autenticação com Efí: " + e.getMessage());
    }
}


    private JSONObject buscarQRCode(int locId) {
    try {
        String accessToken = getAccessToken();
        String baseUrl = credentials.sandbox() ? "https://pix-h.api.efipay.com.br" : "https://pix.api.efipay.com.br";
        String url = baseUrl + "/v2/loc/" + locId + "/qrcode";

        HttpHeaders headers = new HttpHeaders( );
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        log.info("Buscando QR Code para locId: {}", locId);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return new JSONObject(response.getBody());
    } catch (Exception e) {
        log.error("Erro ao buscar QR Code na Efí", e);
        throw new RuntimeException("Falha ao gerar QR Code");
    }
}



    @Transactional
    public void processAndSave(JSONObject response) throws Exception {
        log.info("Processando e salvando dados da cobrança com TxID: {}", response.getString("txid"));
        
        String txid = response.getString("txid");
        String pixCopiaECola = response.getString("pixCopiaECola");
        StatusEfi status = StatusEfi.valueOf(response.getString("status"));
        String valor = response.getJSONObject("valor").getString("original");
        String data = response.getJSONObject("calendario").getString("criacao");
        String cpf = response.getJSONObject("devedor").getString("cpf");
        Integer locId = response.getJSONObject("loc").getInt("id");
        
        PixCharge payment = new PixCharge(txid, valor, status, pixCopiaECola, locId, data, cpf);
        pixRepository.save(payment);
        
        log.info("Cobrança com TxID: {} salva com sucesso no banco de dados.", txid);
    }
}
