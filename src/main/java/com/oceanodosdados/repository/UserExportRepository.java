package com.oceanodosdados.repository;

import com.oceanodosdados.records.UserExportView;
import com.oceanodosdados.domain.UserExport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface UserExportRepository extends JpaRepository<UserExport, String> {
    List<UserExportView> findByUserId(String userId);
}
