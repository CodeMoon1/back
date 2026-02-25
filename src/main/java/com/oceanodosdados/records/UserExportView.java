package com.oceanodosdados.records;

import com.oceanodosdados.enums.Status;

import java.time.LocalDateTime;

public record UserExportView(String urlFile, Status status, LocalDateTime createdAt) {
}
