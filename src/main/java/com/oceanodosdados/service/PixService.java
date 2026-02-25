package com.oceanodosdados.service;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import com.oceanodosdados.config.Credentials;
import com.oceanodosdados.domain.PixCharge;
import com.oceanodosdados.enums.StatusEfi;
import com.oceanodosdados.records.apiPix.PixRequest;
import com.oceanodosdados.repository.PixRepository;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.HashMap;

@Service
public class PixService {

    // 1. Inicializa o logger para esta classe.
    private static final Logger log = LoggerFactory.getLogger(PixService.class);
    private  Credentials credentials;
    private final String keyPix;
    private final PixRepository pixRepository;
    private final String certPath;

    public PixService(
            Credentials credentials,
            @Value("${chave.pix}") String keyPix,
            PixRepository pixRepository
    ) {
        
        this.credentials = credentials;
        this.keyPix = keyPix;
        this.pixRepository = pixRepository;

        this.certPath = credentials.certificate();
        File certFile = new File(this.certPath);

        if (!certFile.exists() || !certFile.canRead()) {
            throw new IllegalStateException(
                    "Certificado não encontrado ou sem permissão: " + this.certPath
            );
        }
        log.info("Ambiente PIX | sandbox={} | cert={}", 
         credentials.sandbox(), certPath);

        log.info("PixService inicializado. Certificado OK em {}", this.certPath);
    }


    private EfiPay criarCliente() {
        try {
            JSONObject options = new JSONObject();
            options.put("client_id", credentials.clientId());
            options.put("client_secret", credentials.clientSecret());
            options.put("certificate", certPath);
            options.put("sandbox", credentials.sandbox());
            options.put("debug", credentials.debug());

            return new EfiPay(options);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao criar cliente EFI", e);
        }
    }


    public JSONObject criaPix(PixRequest pixRequest) {
        EfiPay efi = criarCliente();

        try {
            JSONObject body = new JSONObject();
            body.put("calendario", new JSONObject().put("expiracao", 3600));
            body.put("devedor", new JSONObject()
                    .put("cpf", pixRequest.cpf())
                    .put("nome", pixRequest.nome()));
            body.put("valor", new JSONObject().put("original", "1.00"));
            body.put("chave", keyPix);
            body.put("solicitacaoPagador", "Serviço realizado");

            JSONObject response =
                    efi.call("pixCreateImmediateCharge", new HashMap<>(), body);

            processAndSave(response);
            return response;

        } catch (EfiPayException e) {
            throw new RuntimeException("Erro EFI: " + e.getErrorDescription(), e);
        } catch (Exception e) {
            log.error("Erro técnico ao chamar API PIX", e);
            throw new RuntimeException("Erro técnico PIX", e);
        }
    }


    @Transactional
    public void processAndSave(JSONObject response) throws Exception {
        log.info("Processando e salvando dados da cobrança com TxID: {}", response.getString("txid"));
        // ... (seu código de processamento continua igual)
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

    public JSONObject getPixQrCode(int id) {
    EfiPay efi = criarCliente();

    log.info("Iniciando geração de QR Code para a localização PIX de ID: {}", id);

    HashMap<String, String> params = new HashMap<>();
    params.put("id", String.valueOf(id));

    try {
        JSONObject response =
                efi.call("pixGenerateQRCode", params, new JSONObject());

        String base64Image = response.getString("imagemQrcode");
        byte[] imageBytes = Base64.getDecoder()
                .decode(base64Image.split(",")[1]);

        File outputfile = new File("qrCodeImage.png");
        ImageIO.write(
                ImageIO.read(new ByteArrayInputStream(imageBytes)),
                "png",
                outputfile
        );

        log.info("QR Code salvo em {}", outputfile.getAbsolutePath());
        return response;

    } catch (EfiPayException e) {
        throw new RuntimeException("Erro EFI: " + e.getErrorDescription(), e);
    } catch (Exception e) {
        
        log.error("Erro técnico ao criar PIX", e);
        throw new RuntimeException("Erro técnico ao gerar QR Code", e);
    }
}

}
