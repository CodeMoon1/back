package com.oceanodosdados.ports;

public interface StoragePort {
    String uploadFile(byte[] fileData, String fileName, String contentType);
}
