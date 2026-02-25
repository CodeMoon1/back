package com.oceanodosdados.infra;

import com.oceanodosdados.ports.StoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.exception.SdkClientException;
import com.oceanodosdados.exceptions.StorageException;


@Component
public class S3StorageAdapter implements StoragePort{

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;



     public S3StorageAdapter(@Value("${aws.s3.region}") String region,
                            @Value("${aws.s3.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
        this.region = region;
        this.s3Client = S3Client.builder()
                .region(Region.of(this.region))
                .build();
    }
    @Override
    public String uploadFile(byte[] fileData, String fileName, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(fileData));

            return buildFileUrl(fileName);

        } catch (S3Exception | SdkClientException e) {
            throw new StorageException("Erro ao persistir arquivo no S3", e);
        }
    }

    private String buildFileUrl(String fileName) {
        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, fileName
        );
    }

}
