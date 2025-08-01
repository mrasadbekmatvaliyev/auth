package com.example.auth.controller;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.StatusMessageResponse;
import com.example.auth.dto.UserRequest;
import com.example.auth.dto.UserResponse;
import com.example.auth.dto.VerifyRequest;
import com.example.auth.dto.VerifyResponse;
import com.example.auth.exception.OtpRateLimitException;
import com.example.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@Tag(name = "User Authentication", description = "API endpoints for user registration, login and verification")
public class UserController {

    @Autowired
    private UserService userService;

    // @PostMapping("/add")
    // public UserResponse addUser(@RequestBody UserRequest request) {
    //     return userService.createUserWithOtp(request);
    // }


    @Operation(
        summary = "Register a new user",
        description = "Registers a new user and sends an OTP code. Requires Telegram bot authentication.",
        security = @SecurityRequirement(name = "telegramToken")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully, OTP sent"),
        @ApiResponse(responseCode = "403", description = "Access denied - invalid or missing Telegram token"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addUser(
        @Parameter(description = "Telegram bot authentication token", required = true)
        @RequestHeader(value = "X-Telegram-Token", required = false) String token,
        @Parameter(description = "User registration data", required = true)
        @RequestBody UserRequest request) {
    if (token == null || !token.equals("secret-token-123")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body("Access denied: Only Telegram bot allowed");
    }

    UserResponse response = userService.createUserWithOtp(request);
    return ResponseEntity.ok(response);
    }


    @Operation(
        summary = "Verify OTP code",
        description = "Verifies the OTP code sent to the user during registration."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP verified successfully, user created"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired OTP code")
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest request) {
        try {
            VerifyResponse response = userService.verifyOtpCode(request.getCode());
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                // Return 401 Unauthorized for incorrect/invalid/expired codes
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new StatusMessageResponse("unauthorized", response.getMessage()));
            }
        } catch (RuntimeException e) {
            // Return 401 Unauthorized for any verification errors
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new StatusMessageResponse("unauthorized", e.getMessage()));
        }
    }

    @Operation(
        summary = "Login with Telegram",
        description = "Handles user login via Telegram ID. If no OTP code is provided, generates and sends a new OTP. If OTP code is provided, verifies it and returns authentication token."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success - OTP sent (if no code provided) or login successful (if code provided)"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired OTP code"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "429", description = "Too many OTP requests - rate limit exceeded")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginWithTelegram(
        @Parameter(description = "Login request containing Telegram ID and optional OTP code", required = true)
        @RequestBody LoginRequest request) {
        // If no code is provided, generate and send OTP
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            try {
                UserResponse response = userService.loginWithTelegramId(request.getTelegramId());
                return ResponseEntity.ok(response);
            } catch (OtpRateLimitException e) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new StatusMessageResponse(e.getStatus(), e.getMessage()));
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StatusMessageResponse("user_not_found", e.getMessage()));
            }
        }
        
        // If code is provided, verify it
        try {
            VerifyResponse response = userService.loginWithCode(request.getTelegramId(), request.getCode());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return 401 Unauthorized for incorrect/invalid codes
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new StatusMessageResponse("unauthorized", e.getMessage()));
        }
    }


}
