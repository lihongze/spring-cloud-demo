package com.cloud.transport;

import com.cloud.common.CommonResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author hongze
 * @date
 * @apiNote
 */
@Component
public class RestTransport {
    @Resource
    private RestTemplate restTemplate;

    public CommonResult<?> getFromParam(String url,Object param) {
        return restTemplate.getForObject(url,CommonResult.class,param);
    }

    public CommonResult<?> getFrom(String url) {
        return restTemplate.getForObject(url,CommonResult.class);
    }

    public CommonResult<?> postFromParam(String url,Object param) {
        return restTemplate.postForObject(url,param,CommonResult.class);
    }
}
