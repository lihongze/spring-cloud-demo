package com.cloud.gateway.filter.limiter;

import com.cloud.gateway.filter.model.LimitKey;
import com.cloud.utils.JsonUtils;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author hongze
 * @date
 * @apiNote
 */
public class CustomKeyResolver implements KeyResolver {

    public static final String BEAN_NAME = "customKeyResolver";

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(getKey(exchange));
    }

    private String getKey(ServerWebExchange exchange) {
        LimitKey limitKey = LimitKey.builder()
                .api(exchange.getRequest().getPath().toString())
                .ip(exchange.getRequest().getRemoteAddress().getHostName())
                .biz(exchange.getAttribute("biz"))
                .build();

        return JsonUtils.object2Json(limitKey);
    }
}

