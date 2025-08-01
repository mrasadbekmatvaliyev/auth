package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login request with Telegram ID and optional OTP code")
public class LoginRequest {
    
    @Schema(description = "Telegram user ID", example = "123456789", required = true)
    private String telegramId;
    
    @Schema(description = "OTP code (optional - if not provided, new OTP will be generated)", example = "123456")
    private String code;
}
