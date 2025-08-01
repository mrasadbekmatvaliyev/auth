package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User registration request")
public class UserRequest {
    
    @Schema(description = "User's full name", example = "John Doe", required = true)
    private String name;
    
    @Schema(description = "Telegram user ID", example = "123456789", required = true)
    private String telegramId;
    
    @Schema(description = "User's phone number", example = "+1234567890", required = true)
    private String phoneNumber;
}
