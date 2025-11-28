package cn.edu.seig.vibemusic.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和解析JWT令牌
 */
public class JwtUtil {

    // 密钥（请在生产环境中使用更安全的密钥）
    private static final String SECRET_KEY = "VIBE_MUSIC";

    // JWT过期时间：6小时
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 6;

    /**
     * 生成JWT令牌
     * @param claims 自定义的业务数据（如用户ID、角色等）
     * @return JWT令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return JWT.create()
                .withClaim("claims", claims)                                          // 添加自定义数据
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .sign(Algorithm.HMAC256(SECRET_KEY));                                  // 使用HMAC256算法签名
    }

    /**
     * 解析JWT令牌
     * @param token JWT令牌字符串
     * @return 解析后的自定义业务数据
     */
    public static Map<String, Object> parseToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);
            return jwt.getClaim("claims").asMap();
        } catch (JWTVerificationException e) {
            return null; // 令牌无效或已过期
        }
    }

    /**
     * 验证令牌是否有效
     * @param token JWT令牌字符串
     * @return true-有效，false-无效
     */
    public static boolean validateToken(String token) {
        return parseToken(token) != null;
    }
}


