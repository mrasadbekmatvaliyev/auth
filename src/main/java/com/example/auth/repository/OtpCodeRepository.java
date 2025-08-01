package com.example.auth.repository;

import com.example.auth.entity.OtpCode;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByCodeOrderByCreatedAtDesc(String code);
    
    Optional<OtpCode> findTopByUserOrderByCreatedAtDesc(User user);
    
    Optional<OtpCode> findTopByCodeAndUserOrderByCreatedAtDesc(String code, User user);
    
    @Query("SELECT o FROM OtpCode o WHERE o.user = :user AND o.expiresAt > CURRENT_TIMESTAMP ORDER BY o.expiresAt DESC")
    Optional<OtpCode> findValidCodeByUser(@Param("user") User user);
}

