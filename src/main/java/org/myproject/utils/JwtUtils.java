package org.myproject.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 用于生成 Token 和解析 Token
 */
@Component
public class JwtUtils {
    private static final String SECRET = "my_secret_key_123456"; // 密钥
    private static final long EXPIRE = 3600 * 24 * 7; // 过期时间：7天

    // 生成 Token
    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // 解析 Token
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningAlgorithm(SignatureAlgorithm.HS256).setSigningKey(SECRET)
                .parseClaimsJws(token).getBody().getSubject();
    }
}
