package com.oceanodosdados.service;
import com.oceanodosdados.records.CurrentUserIdRecord;
import com.oceanodosdados.domain.UserExport;
import com.oceanodosdados.records.ReportResponse;
import com.oceanodosdados.reportGenerator.ReportType1;
import com.oceanodosdados.repository.UserExportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.oceanodosdados.records.HubDoDevResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import com.oceanodosdados.ports.FileUploader;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import static org.mockito.Mockito.mock;
import java.io.IOException;
import static org.mockito.Mockito.times;
import com.oceanodosdados.exceptions.StorageException;

class ReportType1ServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserExportRepository userExportRepository;

    @Mock
    private ReportType1 reportType1Generator;

    @Mock
    private FileUploader fileUploader;



    @Autowired
    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportService(reportType1Generator, fileUploader, userExportRepository, userService);
    }

    @DisplayName("Nenhum dado válido encontrado para geração do relatório.")
    @Test
    void shouldReturnErrorWhenNoValidData() {

        HubDoDevResponse response = Mockito.mock(HubDoDevResponse.class);
        when(response.status()).thenReturn(false);
        when(response.result()).thenReturn(null);

        Set<HubDoDevResponse> responses = Set.of(response);

        when(userService.getCurrentUserIdRecord())
                .thenReturn(new CurrentUserIdRecord("user-123"));

        ReportResponse r = reportService.FileGenerateAndUploadAndPersist(responses);

        assertFalse(r.success());
        assertEquals(r.url(),"Indisponivel");

        assertEquals("Verificar arquivo enviado: Nenhum dado disponível para análise", r.message());

        verify(userExportRepository, times(1)).save(any(UserExport.class));
        verifyNoInteractions(fileUploader);
    }




    @DisplayName("Fluxo de sucesso ao gerar, fazer upload e persistir o arquivo")
    @Test
    void shouldGenerateUploadAndPersistSuccessfully() throws IOException{
        HubDoDevResponse.Result result = new HubDoDevResponse.Result(
                "123",
                "Teste Silva",
                "M",
                "1998-01-01",
                "12345678900",
                "Maria Souza",
                26,
                "Capricórnio",
                List.of(),
                List.of(),
                List.of(),
                "5000",
                "ATIVO",
                "2024-01-01",
                "2024-12-31"
        );

        HubDoDevResponse response = mock(HubDoDevResponse.class);

        when(response.status()).thenReturn(true);
        when(response.result()).thenReturn(result);

        Set<HubDoDevResponse> responses = Set.of(response);

        when(userService.getCurrentUserIdRecord())
                .thenReturn(new CurrentUserIdRecord("user-123"));

        byte[] fakeExcel = new byte[]{1, 2, 3};

        when(reportType1Generator.generate(responses)).thenReturn(fakeExcel);

        when(fileUploader.upload(fakeExcel)).thenReturn("http://example.com/report.xlsx");

        ReportResponse r = reportService.FileGenerateAndUploadAndPersist(responses);

        verify(reportType1Generator, times(1))
                .generate(responses);

        verify(fileUploader, times(1))
                .upload(fakeExcel);


        assertTrue(r.success());
        assertNotNull(r.url());

        verify(reportType1Generator).generate(responses);
        verify(fileUploader).upload(fakeExcel);

        assertEquals(r.url(), "http://example.com/report.xlsx");
        assertEquals(r.message(), "Arquivo gerado com sucesso");
        assertEquals(r.success(), true);
        verify(userExportRepository, times(1)).save(any(UserExport.class));
    }


    @DisplayName("Erro ao fazer upload do arquivo no bucket s3")
    @Test
    void shouldThrowsExS3() throws IOException{
        HubDoDevResponse.Result result = new HubDoDevResponse.Result(
                "123",
                "Teste Silva",
                "M",
                "1998-01-01",
                "12345678900",
                "Maria Souza",
                26,
                "Capricórnio",
                List.of(),
                List.of(),
                List.of(),
                "5000",
                "ATIVO",
                "2024-01-01",
                "2024-12-31"
        );

        HubDoDevResponse response = mock(HubDoDevResponse.class);

        when(response.status()).thenReturn(true);
        when(response.result()).thenReturn(result);

        Set<HubDoDevResponse> responses = Set.of(response);

        when(userService.getCurrentUserIdRecord())
                .thenReturn(new CurrentUserIdRecord("user-123"));

        byte[] fakeExcel = new byte[]{1, 2, 3};

        when(reportType1Generator.generate(responses)).thenReturn(fakeExcel);

        when(fileUploader.upload(any(byte[].class)))
                .thenThrow(new StorageException("Upload failed", new RuntimeException()));


        ReportResponse r = reportService.FileGenerateAndUploadAndPersist(responses);


        assertEquals(r.success(), false);

        assertEquals(r.message(), "Por gentileza, tente novamente em alguns instantes.");
        assertEquals(r.url(), "Indisponivel");
    }

    @DisplayName("Erro ao criar o arquivo Excel")
    @Test
    void shouldThrowsExReportGenerator() throws IOException{
        HubDoDevResponse.Result result = new HubDoDevResponse.Result(
                "123",
                "Teste Silva",
                "M",
                "1998-01-01",
                "12345678900",
                "Maria Souza",
                26,
                "Capricórnio",
                List.of(),
                List.of(),
                List.of(),
                "5000",
                "ATIVO",
                "2024-01-01",
                "2024-12-31"
        );

        HubDoDevResponse response = mock(HubDoDevResponse.class);

        when(response.status()).thenReturn(true);
        when(response.result()).thenReturn(result);

        Set<HubDoDevResponse> responses = Set.of(response);

        when(userService.getCurrentUserIdRecord())
                .thenReturn(new CurrentUserIdRecord("user-123"));

        byte[] fakeExcel = new byte[]{1, 2, 3};

        when(reportType1Generator.generate(any(Set.class))).thenThrow(new IOException(" Generation failed"));

        ReportResponse r = reportService.FileGenerateAndUploadAndPersist(responses);


        assertEquals(r.success(), false);
        assertEquals(r.message(), "Erro ao escrever arquivo Excel.");
        assertEquals(r.url(), "Indisponivel");
    }
}