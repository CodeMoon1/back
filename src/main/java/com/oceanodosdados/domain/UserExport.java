package com.oceanodosdados.domain;


import java.time.LocalDateTime;
import com.oceanodosdados.enums.Status;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
@Entity
@Table(name = "user_export")
public class UserExport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    private String urlFile;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public LocalDateTime getCreateAt(){
        return createdAt;
    }
    
    public UserExport() {
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}