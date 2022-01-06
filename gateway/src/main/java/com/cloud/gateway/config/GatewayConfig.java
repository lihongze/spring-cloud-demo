package com.cloud.gateway.config;

import com.cloud.gateway.filter.limiter.CustomKeyResolver;
import com.cloud.gateway.filter.limiter.CustomRedisRateLimiter;
import com.cloud.gateway.filter.limiter.RateLimiterFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * @author hongze
 * @date
 * @apiNote
 */
@Component
public class GatewayConfig {
    @Bean
    @Primary
    public CustomRedisRateLimiter customRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                                         Validator validator) {
        return new CustomRedisRateLimiter(redisTemplate, validator);
    }

    @Bean
    public RateLimiterFactory rateLimiterFactory(CustomRedisRateLimiter customRedisRateLimiter, CustomKeyResolver customKeyResolver) {
        return new RateLimiterFactory(customRedisRateLimiter, customKeyResolver);
    }
}
