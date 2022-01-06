package com.cloud.gateway.filter.model;

import lombok.Data;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;

import java.util.Map;

/**
 * @author hongze
 * @date
 * @apiNote
 */
@Data
public class LimitConfig {
    private String routeId;
    Map<String, RedisRateLimiter.Config> tokenConfig;
}