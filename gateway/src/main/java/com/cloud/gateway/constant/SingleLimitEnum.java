package com.cloud.gateway.constant;

import com.cloud.common.constant.WebConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author hongze
 * @date
 * @apiNote
 */
public enum SingleLimitEnum {
    API_LIMIT(WebConstants.API_LIMIT,RateLimit.RateLimitConfig.API),
    BIZ_LIMIT(WebConstants.BIZ_LIMIT,RateLimit.RateLimitConfig.BIZ),
    IP_LIMIT(WebConstants.IP_LIMIT,RateLimit.RateLimitConfig.IP);

    @Getter
    private String roteType;
    @Getter
    private RateLimit rateLimit;

    SingleLimitEnum(String roteType,RateLimit rateLimit) {
        this.roteType = roteType;
        this.rateLimit = rateLimit;
    }

    public static RateLimit getLevelEnum(String roteType) {
        for (SingleLimitEnum typeEnum : SingleLimitEnum.values()) {
            if (Objects.equals(typeEnum.roteType,roteType)) {
                return typeEnum.rateLimit;
            }
        }
        return null;
    }

    public interface RateLimit{
        int getBurstCapacity();
        int getReplenishRate();

        @AllArgsConstructor
        enum RateLimitConfig implements RateLimit {
            API {
                @Override
                public int getBurstCapacity() {
                    return 5;
                }

                @Override
                public int getReplenishRate() {
                    return 5;
                }
            },
            BIZ {
                @Override
                public int getBurstCapacity() {
                    return 6;
                }

                @Override
                public int getReplenishRate() {
                    return 6;
                }
            },
            IP {
                @Override
                public int getBurstCapacity() {
                    return 7;
                }

                @Override
                public int getReplenishRate() {
                    return 7;
                }
            }

        }
    }
}
