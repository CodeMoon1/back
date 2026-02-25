package com.oceanodosdados.service;

import java.util.Set;

import com.oceanodosdados.enums.Status;
import org.springframework.stereotype.Service;
import com.oceanodosdados.records.UserExportView;
import java.util.List;
import com.oceanodosdados.reportGenerator.IReport;
import com.oceanodosdados.domain.UserExport;
import com.oceanodosdados.records.HubDoDevResponse;
import com.oceanodosdados.records.ReportResponse;
import com.oceanodosdados.repository.UserExportRepository;
import lombok.extern.slf4j.Slf4j;
import com.oceanodosdados.exceptions.StorageException;

import com.oceanodosdados.ports.FileUploader;

@Slf4j
@Service
public class ReportService {

    private UserService userService;

    private UserExportRepository userExportRepository;

    private IReport report;

    private FileUploader fileUploader;

    private static final String DEFAULT_ERROR_URL = "Indisponivel";
    private static final String SUCCESS_MESSAGE = "Arquivo gerado com sucesso";
    private static final String NO_DATA_MESSAGE = "Verificar arquivo enviado: Nenhum dado disponível para análise";
    private static final String GENERATION_ERROR_MESSAGE = "Erro ao escrever arquivo Excel.";
    private static final String UPLOAD_ERROR_MESSAGE = "Por gentileza, tente novamente em alguns instantes.";
    private static final String PERSISTENCE_ERROR_MESSAGE = "Erro ao persistir na database.";

    public ReportService(IReport report, FileUploader fileUploader, UserExportRepository userExportRepository, UserService userService) {
        this.report = report;
        this.fileUploader = fileUploader;
        this.userExportRepository = userExportRepository;
        this.userService = userService;
    }

    public ReportResponse FileGenerateAndUploadAndPersist(Set<HubDoDevResponse> cpfRespons) {
        boolean validResponses = isEmpty(cpfRespons);

        if (validResponses)
            return createErrorResponse(NO_DATA_MESSAGE, new Exception(NO_DATA_MESSAGE));
        byte[] excel;

        try {
            excel = report.generate(cpfRespons);
        } catch (Exception e) {
            return createErrorResponse(GENERATION_ERROR_MESSAGE, e);
        }
        String url = null;
        try {
            url = fileUploader.upload(excel);
        } catch (StorageException e) {
            return createErrorResponse(UPLOAD_ERROR_MESSAGE, e);
        }

        try {
            persistFile(url, Status.SUCCESS);
        } catch (Exception e) {
            return createErrorResponse(PERSISTENCE_ERROR_MESSAGE, e);
        }

        return createSuccessResponse(url);
    }

    private ReportResponse createErrorResponse(String message, Exception e) {
        log.error("{}: {}", message, e.getMessage());
        persistFile(DEFAULT_ERROR_URL, Status.ERROR);
        return new ReportResponse(
                false,
                DEFAULT_ERROR_URL,
                message
        );
    }


    private ReportResponse createSuccessResponse(String url) {
        return new ReportResponse(
                true,
                url,
                SUCCESS_MESSAGE
        );
    }

    private void persistFile(String url, Status status) {
        UserExport userExport = new UserExport();
        userExport.setUserId(userService.getCurrentUserIdRecord().id());
        userExport.setUrlFile(url);
        userExport.setStatus(status);
        userExportRepository.save(userExport);
    }

    public List<UserExportView> getAllExportsByUserId() {
        return userExportRepository.findByUserId(userService.getCurrentUserIdRecord().id());
    }

    private boolean isEmpty(Set<HubDoDevResponse> cpfRespons) {
        List<HubDoDevResponse> validResponses = cpfRespons.stream()
                .filter(HubDoDevResponse::status)
                .filter(r -> r.result() != null)
                .toList();

        if (validResponses.isEmpty())
            return true;
        return false;
    }

}
