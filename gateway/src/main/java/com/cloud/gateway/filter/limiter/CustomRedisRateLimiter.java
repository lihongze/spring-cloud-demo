package com.cloud.gateway.filter.limiter;

import com.cloud.common.constant.WebConstants;
import com.cloud.gateway.constant.SingleLimitEnum;
import com.cloud.gateway.filter.model.LimitConfig;
import com.cloud.gateway.filter.model.LimitKey;
import com.cloud.utils.EmptyUtils;
import com.cloud.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hongze
 * @date
 * @apiNote
 */
@Slf4j
public class CustomRedisRateLimiter extends RedisRateLimiter {
    private ReactiveRedisTemplate<String, String> redisTemplate;
    // 单机限流lua脚本
    private RedisScript<List<Long>> singleScript;
    // 并发限流lua脚本
    private RedisScript<List<Long>> concurrentScript;
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private RedisRateLimiter.Config defaultConfig;
    private Map<String,Config> tokenMap;

    public CustomRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate, Validator validator) {
        super(redisTemplate,null,validator);
        this.singleScript = this.getSingleScript();
        this.concurrentScript = this.getConcurrentScript();
        this.initialized.compareAndSet(false, true);
        this.tokenMap = initTokenMap();
    }

    public DefaultRedisScript<List<Long>> getSingleScript() {
        DefaultRedisScript<List<Long>> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/request_rate_limiter.lua")));
        return redisScript;
    }

    public DefaultRedisScript<List<Long>> getConcurrentScript() {
        DefaultRedisScript<List<Long>> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/concurrent_requests_limiter.lua")));
        return redisScript;
    }

    static List<String> getSingleKeys(String id) {
        String prefix = "request_rate_limiter.lua.{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    static List<String> getConcurrentKeys(String id) {
        String prefix = "concurrent_requests_limiter.lua.{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    /**
     * This uses a basic token bucket algorithm and relies on the fact that Redis scripts
     * execute atomically. No other operations can run between fetching the count and
     * writing the new count.
     */
    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("RedisRateLimiter is not initialized");
        }

        LimitConfig limitConfig = getLimitConfig(routeId);
        Config singleApiConf = getDefaultConfig(WebConstants.SINGLE_LIMIT,WebConstants.API_LIMIT);
        Config bizApiConf = getDefaultConfig(WebConstants.SINGLE_LIMIT,WebConstants.BIZ_LIMIT);
        Config ipApiConf = getDefaultConfig(WebConstants.SINGLE_LIMIT,WebConstants.IP_LIMIT);

        if (limitConfig == null || limitConfig.getTokenConfig().size()==0) {
            return Mono.just(new Response(true,null));
        }

        Map<String, Config> conf = limitConfig.getTokenConfig();
        LimitKey limitKey = JsonUtils.json2Object(id, LimitKey.class);
        //api限流
        String api = limitKey.getApi();
        Config apiConf = EmptyUtils.isEmpty(conf.get(api)) ? singleApiConf : conf.get(api);
        //业务方限流
        String biz = limitKey.getBiz();
        Config bizConf = EmptyUtils.isEmpty(conf.get(biz)) ? bizApiConf : conf.get(biz);
        //ip限流
        String ip = limitKey.getIp();
        Config ipConf = EmptyUtils.isEmpty(conf.get(ip)) ? ipApiConf : conf.get(ip);

        if (apiConf != null) {
            return isSingleAllow(api, routeId, apiConf).flatMap(res -> {
                if (res.isAllowed()) {
                    return isSingleAllow(biz, routeId, bizConf).flatMap(userRes -> {
                        if (userRes.isAllowed()) {
                            return isSingleAllow(ip, routeId, ipConf);
                        }
                        return Mono.just(userRes);
                    });
                }
                return Mono.just(res);
            });
        }
        return Mono.just(new Response(true, new HashMap<>()));
    }

    /**
     * 单级限流
     */
    private Mono<Response> isSingleAllow(String key, String routeId, Config config) {
        // How many requests per second do you want a user to be allowed to do?
        int replenishRate = config.getReplenishRate();

        // How much bursting do you want to allow?
        int burstCapacity = config.getBurstCapacity();

        try {
            List<String> keys = getSingleKeys(routeId+"$"+key);

            // The arguments to the LUA script. time() returns unixtime in seconds.
            List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "",
                    Instant.now().getEpochSecond() + "", "1");
            // allowed, tokens_left = redis.eval(SCRIPT, keys, args)
            Flux<List<Long>> flux = this.redisTemplate.execute(this.singleScript, keys, scriptArgs);
            // .log("redisratelimiter", Level.FINER);
            return flux.onErrorResume(throwable -> Flux.just(Arrays.asList(1L, -1L)))
                    .reduce(new ArrayList<Long>(), (longs, l) -> {
                        longs.addAll(l);
                        return longs;
                    }) .map(results -> {
                        boolean allowed = results.get(0) == 1L;
                        Long tokensLeft = results.get(1);

                        Response response = new Response(allowed, getHeaders(config, tokensLeft));

                        if (log.isDebugEnabled()) {
                            log.debug("response: " + response);
                        }
                        return response;
                    });
        }
        catch (Exception e) {
            /*
             * We don't want a hard dependency on Redis to allow traffic. Make sure to set
             * an alert so you know if this is happening too much. Stripe's observed
             * failure rate is 0.01%.
             */
            log.error("Error determining if user allowed from redis", e);
        }
        return Mono.just(new Response(true, getHeaders(config, -1L)));
    }

    /**
     * 并发限流 TODO
     */
    private Mono<Response> isConcurrentAllow(String key, String routeId, Config config) {
        return Mono.just(new Response(true, getHeaders(config, -1L)));
    }

    private Map<String,Config> initTokenMap() {
        Map<String, Config> tokenMap = new HashMap<>();
        Config apiConfig = new Config();
        apiConfig.setBurstCapacity(2);
        apiConfig.setReplenishRate(4);

        Config bizConfig = new Config();
        bizConfig.setBurstCapacity(2);
        bizConfig.setReplenishRate(4);

        tokenMap.put("/rateLimit", apiConfig);
        tokenMap.put("biz", bizConfig);
        return tokenMap;
    }

    // 不同的routeId可配置不同的限流配置
    private LimitConfig getLimitConfig(String routeId) {
        LimitConfig limitConfig = new LimitConfig();
        limitConfig.setRouteId(routeId);
        limitConfig.setTokenConfig(tokenMap);
        return limitConfig;
    }

    private Config getDefaultConfig(String limitType,String roteType) {
        Config config = newConfig();
        if (Objects.equals(limitType, WebConstants.SINGLE_LIMIT)) {
            SingleLimitEnum.RateLimit rateLimit = SingleLimitEnum.getLevelEnum(roteType);
            if (!EmptyUtils.isEmpty(rateLimit)) {
                config.setBurstCapacity(rateLimit.getBurstCapacity());
                config.setBurstCapacity(rateLimit.getReplenishRate());
                return config;
            }
        }
        if (Objects.equals(limitType, WebConstants.CONCURRENT_LIMIT)) {
            // concurrent limit TODO
        }
        return null;
    }

}
