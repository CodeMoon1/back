package com.oceanodosdados.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.oceanodosdados.domain.PixCharge;

public interface PixRepository extends JpaRepository<PixCharge, String> {
    
}
