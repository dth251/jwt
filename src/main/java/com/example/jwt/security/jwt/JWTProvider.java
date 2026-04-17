package com.example.jwt.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JWTProvider {

    @Value("${jwt.sercret-key}")
    private String secretKey;

    @Value("${jwt.expired}")
    private long expiration;

    /**
     * Tạo SecretKey từ chuỗi base64 hoặc plain string
     */
    private SecretKey getSigningKey() {
        // Nếu key đủ dài (plain string) – convert sang bytes
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Tạo JWT token từ username
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Tạo refresh token (thời gian dài hơn 10x)
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration * 10))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Xác thực token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("JWT signature mismatch: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims empty or invalid: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Lấy username từ token
     */
    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
