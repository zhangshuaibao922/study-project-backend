package com.example.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    String sendValidateEmail(String email, String id);
    String validateAndRegister(String username,String password,String email,String code,String sessionId);
}
