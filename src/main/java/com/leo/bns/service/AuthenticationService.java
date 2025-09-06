package com.leo.bns.service;

import com.leo.bns.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    @Autowired
    private Environment env;
    
    public void validateApiPassword(String providedPassword) {
        if (providedPassword == null || providedPassword.trim().isEmpty()) {
            logger.warn("Authentication attempt with empty password");
            throw new UnauthorizedException("Password is required");
        }
        
        String correctPassword = env.getProperty("API_PASSWORD");
        if (correctPassword == null) {
            logger.error("API_PASSWORD not configured");
            throw new RuntimeException("Server configuration error");
        }
        
        if (!providedPassword.equals(correctPassword)) {
            logger.warn("Authentication failed: Incorrect password provided");
            throw new UnauthorizedException("Invalid credentials");
        }
        
        logger.debug("Authentication successful");
    }
}
