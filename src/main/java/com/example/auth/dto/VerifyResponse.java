package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response after OTP verification or login")
public class VerifyResponse {
    
    @Schema(description = "Whether the operation was successful", example = "true")
    private boolean success;
    
    @Schema(description = "Response message", example = "Login successful")
    private String message;
    
    @Schema(description = "User's name", example = "John Doe")
    private String name;
    
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;
    
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}
