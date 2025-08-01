package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response containing OTP code")
public class UserResponse {
    
    @Schema(description = "OTP code sent to user", example = "123456")
    private String code;
}
