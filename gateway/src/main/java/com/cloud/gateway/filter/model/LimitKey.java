package com.cloud.gateway.filter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hongze
 * @date
 * @apiNote
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LimitKey {
    private String api;
    private String biz;
    private String ip;
}
