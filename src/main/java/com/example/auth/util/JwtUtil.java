package com.example.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // 256-bit secret key (yaxshi amaliyot - .env yoki config faylda saqlash)
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token muddati: 1 soat
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    public static String generateToken(Long userId, String telegramId, String phoneNumber) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("telegramId", telegramId)
                .claim("phone", phoneNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static Key getKey() {
        return key;
    }
}
