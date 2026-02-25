    package com.oceanodosdados.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.oceanodosdados.client.HubDoDevClient;
import com.oceanodosdados.records.HubDoDevResponse;


@Service
public class HubDoDevService {
    
    private final HubDoDevClient hubDoDevClient;

    public HubDoDevService(HubDoDevClient hubDoDevClient) {
        this.hubDoDevClient = hubDoDevClient;
    }   
    
    public HubDoDevResponse getCadastroPF(String cpf) {
        try {
            return hubDoDevClient.getCadastroPF(cpf, "");
        } catch (Exception e) {
            return createErrorResponse(cpf, e);
        }
    }

    public Set<HubDoDevResponse> getDadosPF(Set<String> cpfs) {
        return cpfs.stream()
                .map(this::getCadastroPF) 
                .collect(Collectors.toSet());
    }
    
    private HubDoDevResponse createErrorResponse(String cpf, Exception ex) {
        return new HubDoDevResponse(
            false,
            null,
            null,
            "Erro ao processar CPF " + cpf + ": " + ex.getMessage(),
            null
        );
    }
}