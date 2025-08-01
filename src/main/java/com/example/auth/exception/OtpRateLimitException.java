package com.example.auth.exception;

public class OtpRateLimitException extends RuntimeException {
    private final String status;
    
    public OtpRateLimitException(String status, String message) {
        super(message);
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
}
