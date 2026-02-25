package com.oceanodosdados.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oceanodosdados.records.ReportRequest;
import com.oceanodosdados.records.ReportResponse;
import com.oceanodosdados.service.HubDoDevService;
import com.oceanodosdados.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ReportController {

    private final HubDoDevService hubDoDevService;

    private ReportService reportService;

    public ReportController(HubDoDevService hubDoDevService, ReportService reportService) {
        this.hubDoDevService = hubDoDevService;
        this.reportService = reportService;
    }
    
    @PostMapping("/cpf")
    public ResponseEntity<ReportResponse> getReport(@RequestBody List<ReportRequest> body) {

        try {
            Set<String> cpfs = body.stream()
                    .map(ReportRequest::cpf)
                    .collect(Collectors.toSet());

            ReportResponse response = reportService.FileGenerateAndUploadAndPersist(
                    hubDoDevService.getDadosPF(cpfs)
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao processar lista de CPFs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ReportResponse(false , null ,e.getMessage()));
        }
    }


    @PostMapping("/placas")
    public ResponseEntity<ReportResponse> getReportPlate(
            @RequestBody List<ReportRequest> body) {

        try {
            throw new UnsupportedOperationException(
                    "Relatório por placa não implementado"
            );

        } catch (Exception e) {
            log.error("Erro ao processar lista de Placas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ReportResponse(false, null, e.getMessage()));
        }
    }

}
