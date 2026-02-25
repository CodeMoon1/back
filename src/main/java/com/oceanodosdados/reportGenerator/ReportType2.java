package com.oceanodosdados.reportGenerator;

import com.oceanodosdados.records.HubDoDevResponse;

import java.io.IOException;
import java.util.Set;

public class ReportType2 implements IReport {
    @Override
    public byte[] generate(Set<HubDoDevResponse> roots) throws IOException {
        return new byte[0];
    }
}
