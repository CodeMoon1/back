package com.oceanodosdados.records;

public record ReportResponse(
    boolean success,
    String url,
    String message) {
}
