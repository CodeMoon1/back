package com.oceanodosdados.reportGenerator;
import com.oceanodosdados.records.HubDoDevResponse;
import java.util.Set;
import java.io.IOException;
public interface IReport {
    byte[] generate(Set<HubDoDevResponse> roots) throws IOException;
}
