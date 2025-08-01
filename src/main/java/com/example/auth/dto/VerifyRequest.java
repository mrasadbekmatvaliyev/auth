package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "OTP verification request")
public class VerifyRequest {
    
    @Schema(description = "OTP code to verify", example = "123456", required = true)
    private String code;
}
