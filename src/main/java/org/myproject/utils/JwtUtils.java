package org.myproject.utils;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.jsonwebtoken.security.Keys;


/**
 * 用于生成 Token 和解析 Token
 */
@Component
public class JwtUtils {
    private static final long EXPIRE = 3600 * 24 * 7; // 过期时间：7天


    // 密钥至少需要 32 个字符
    private static final String SECRET = "your_super_secret_key_at_least_32_characters";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));



    // 生成 Token
    public String createToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + 3600_000)) // 1小时
                .signWith(KEY)
                .compact();
    }

    // 解析 Token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
