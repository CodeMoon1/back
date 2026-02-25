package com.oceanodosdados.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.oceanodosdados.domain.User;
import com.oceanodosdados.records.CurrentUserIdRecord;
import com.oceanodosdados.repository.UserExportRepository;

@Service
public class UserService {

    @Autowired
    private UserExportRepository userExportRepository;

    private CurrentUserIdRecord CurrentUserIdRecord;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new RuntimeException("Usuário não autenticado");
        }
    }

    public CurrentUserIdRecord getCurrentUserIdRecord(){
        User currentUser = getAuthenticatedUser();
        CurrentUserIdRecord = new CurrentUserIdRecord(currentUser.getId());
        return CurrentUserIdRecord;
    }

}
