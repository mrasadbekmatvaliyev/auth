package com.example.auth.service;
import com.example.auth.util.JwtUtil;
import com.example.auth.dto.UserRequest;
import com.example.auth.dto.UserResponse;
import com.example.auth.dto.VerifyResponse;
import com.example.auth.exception.OtpRateLimitException;
import com.example.auth.entity.OtpCode;
import com.example.auth.entity.User;
import com.example.auth.repository.OtpCodeRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpCodeRepository otpCodeRepository;

    public UserResponse createUserWithOtp(UserRequest request) {
        Optional<User> optionalUser = userRepository.findByTelegramId(request.getTelegramId());

        User user = optionalUser.orElseGet(() -> User.builder()
                .name(request.getName())
                .telegramId(request.getTelegramId())
                .phoneNumber(request.getPhoneNumber())
                .build());

        user = userRepository.save(user);

        String otp = generateOtp();

        OtpCode otpCode = OtpCode.builder()
                .code(otp)
                .used(false)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();

        otpCodeRepository.save(otpCode);

        return new UserResponse(otp);
    }

    private String generateOtp() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    public VerifyResponse verifyOtpCode(String code) {
    Optional<OtpCode> otpOptional = otpCodeRepository.findTopByCodeOrderByCreatedAtDesc(code);

    if (otpOptional.isEmpty()) {
        return new VerifyResponse(false, "Invalid OTP code", null, null, null);
    }

    OtpCode otp = otpOptional.get();

    // 2 daqiqalik muddat tekshirish (optional)
    if (otp.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(2))) {
        return new VerifyResponse(false, "OTP expired", null, null, null);
    }

    User user = otp.getUser();

    String token = JwtUtil.generateToken(user.getId(), user.getTelegramId(), user.getPhoneNumber());

    return new VerifyResponse(true, "Login successful", user.getName(), user.getPhoneNumber(), token);
}

public UserResponse loginWithTelegramId(String telegramId) {
    Optional<User> optionalUser = userRepository.findByTelegramId(telegramId);

    if (optionalUser.isEmpty()) {
        throw new RuntimeException("User not found with this Telegram ID");
    }

    User user = optionalUser.get();

    // Check if there's a recent OTP created (within 2 minutes)
    Optional<OtpCode> recentOtp = otpCodeRepository.findTopByUserOrderByCreatedAtDesc(user);
    
    if (recentOtp.isPresent()) {
        LocalDateTime lastOtpTime = recentOtp.get().getCreatedAt();
        long minutesSinceLastOtp = ChronoUnit.MINUTES.between(lastOtpTime, LocalDateTime.now());
        

        if (minutesSinceLastOtp < 2) {
            long waitMinutes = 2 - minutesSinceLastOtp;
            throw new OtpRateLimitException("rate_limit_exceeded", "Please wait " + waitMinutes + " more minute(s) before requesting a new OTP.");
        }
    }

    // Check for existing valid OTP
    Optional<OtpCode> existingValidOtp = otpCodeRepository.findValidCodeByUser(user);

    String otpCode;

    if (existingValidOtp.isPresent()) {
        otpCode = existingValidOtp.get().getCode();  // return existing valid code
    } else {
        // Create new OTP code
        otpCode = generateOtp();

        OtpCode newOtp = OtpCode.builder()
                .code(otpCode)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(2))
                .build();

        otpCodeRepository.save(newOtp);
    }

    return new UserResponse(otpCode);
}

public VerifyResponse loginWithCode(String telegramId, String code) {
    Optional<User> optionalUser = userRepository.findByTelegramId(telegramId);

    if (optionalUser.isEmpty()) {
        throw new RuntimeException("User not found with this Telegram ID");
    }

    User user = optionalUser.get();

    // Find the OTP code for this user
    Optional<OtpCode> otpOptional = otpCodeRepository.findTopByCodeAndUserOrderByCreatedAtDesc(code, user);

    if (otpOptional.isEmpty()) {
        throw new RuntimeException("Invalid OTP code");
    }

    OtpCode otp = otpOptional.get();

    // Check if OTP has expired (2 minutes)
    if (otp.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(2))) {
        throw new RuntimeException("OTP has expired");
    }

    // Check if OTP is already used
    if (otp.isUsed()) {
        throw new RuntimeException("OTP has already been used");
    }

    // Mark OTP as used
    otp.setUsed(true);
    otpCodeRepository.save(otp);

    // Generate JWT token
    String token = JwtUtil.generateToken(user.getId(), user.getTelegramId(), user.getPhoneNumber());

    return new VerifyResponse(true, "Login successful", user.getName(), user.getPhoneNumber(), token);
}


}
