// dto/StatusMessageResponse.java
package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Error response with status and message")
public class StatusMessageResponse {
    
    @Schema(description = "Error status code", example = "unauthorized")
    private String status;
    
    @Schema(description = "Error message", example = "Invalid or expired OTP code")
    private String message;
}
