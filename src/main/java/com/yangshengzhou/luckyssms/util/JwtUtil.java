package com.yangshengzhou.luckyssms.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime; // 单位是秒

    // 生成Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date()) // 添加签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // 从Token中提取所有声明
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // 从Token中提取Uid
    public String extractUid(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 检查Token是否过期
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 验证Token是否有效
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUid(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}