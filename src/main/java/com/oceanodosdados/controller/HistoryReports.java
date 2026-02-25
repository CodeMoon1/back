package com.oceanodosdados.controller;
import com.oceanodosdados.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.oceanodosdados.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.oceanodosdados.domain.UserExport;
import java.util.List;
import com.oceanodosdados.records.UserExportView;
@RestController
@RequestMapping("/api/v1/relatorios")
public class HistoryReports {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<List<UserExportView>>historyReports() {
        return ResponseEntity.ok(reportService.getAllExportsByUserId());
    }
}
