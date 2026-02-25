package com.oceanodosdados.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.oceanodosdados.records.apiPix.PixRequest;
import com.oceanodosdados.service.PixService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/pix" )
@RequiredArgsConstructor // Injeção de dependência via construtor (Lombok)
@Slf4j
public class PixController {

    private final PixService pixService; // Campo final para garantir imutabilidade

    @PostMapping("/charge")
public ResponseEntity<?> pix(@RequestBody PixRequest pixRequest) {
    log.info("Recebida requisição de cobrança Pix para: {}", pixRequest.nome());
    try {
        var response = pixService.criaPix(pixRequest);
        
        // CORREÇÃO: Verifica se o service retornou null antes de usar o toString()
        if (response == null) {
            log.error("Falha na criação do Pix: O Service retornou null.");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar cobrança Pix. Verifique as credenciais ou a conexão com o provedor.");
        }
        
        return ResponseEntity.ok(response.toString());
        
    } catch (Exception e) {
        log.error("Erro inesperado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno.");
    }
}
}