package az.spring.config;

import az.spring.exception.TokenIsNotExists;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RedisTokenService {
    private RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "TOKEN:";

    public RedisTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(Long userId) {
        return TOKEN_PREFIX + UUID.randomUUID();
    }

    public void saveToken(Long userId, String token) {
        String key = TOKEN_PREFIX + token;
        String userIdString = userId.toString();
        redisTemplate.opsForValue().set(key, userIdString);
        redisTemplate.expire(key, Duration.ofSeconds(30));
    }

    public boolean isValidToken(String token) {
        String userIdString = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        return userIdString != null;
    }

    public boolean hasTokenExpired(String token) {
        String key = TOKEN_PREFIX + token;

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Long expirationTime = valueOperations.getOperations().getExpire(key);

        return expirationTime != null && expirationTime <= 0;
    }

    public String resetToken(String oldToken) {
        String userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + oldToken);
        if (userId != null) {
            Long userIdLong = Long.valueOf(userId);
            String newToken = generateToken(userIdLong);

            redisTemplate.delete(TOKEN_PREFIX + oldToken);
            redisTemplate.opsForValue().set(TOKEN_PREFIX + newToken, userId);
            redisTemplate.expire(TOKEN_PREFIX + newToken, Duration.ofHours(1));

            return newToken;
        } else {
            throw new TokenIsNotExists("Token does not exist or has expired");
        }
    }

}
