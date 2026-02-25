package com.oceanodosdados.ports;

import com.oceanodosdados.ports.StoragePort;
import com.oceanodosdados.ports.FileUploader;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class S3FileUploader implements FileUploader {


    private final StoragePort storagePort;

    public S3FileUploader(StoragePort storagePort) {
        this.storagePort = storagePort;
    }

    @Override
    public String upload(byte[] file) {
        String fileName = UUID.randomUUID() + ".xlsx";

        return storagePort.uploadFile(
                file,
                fileName,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
    }
}
