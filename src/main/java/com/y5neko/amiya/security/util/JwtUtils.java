package com.y5neko.amiya.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // 生成一个随机密钥
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 默认过期时间：1小时
    private static final long DEFAULT_EXPIRATION = 60 * 60 * 1000L;

    /**
     * 生成JWT
     * @param claims 载荷信息
     * @param expirationMillis 过期时间（毫秒）
     * @return JWT字符串
     */
    public static String generateToken(Map<String, Object> claims, long expirationMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 生成JWT，使用默认过期时间
     * @param claims 载荷信息
     * @return JWT字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return generateToken(claims, DEFAULT_EXPIRATION);
    }

    /**
     * 解析JWT
     * @param token JWT字符串
     * @return 载荷信息
     */
    public static Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断JWT是否过期
     * @param token JWT字符串
     * @return 是否过期
     */
    public static boolean isExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "y5neko");
        claims.put("role", "admin");
        String token = generateToken(claims);
        System.out.println(token);
        System.out.println(isExpired(token));
        Claims claims2 = parseToken(token);
        System.out.println(claims2);
    }
}
