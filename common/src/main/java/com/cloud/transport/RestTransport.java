package com.cloud.transport;

import com.cloud.common.CommonResult;
import com.cloud.utils.ResponseUtils;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
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

    @HystrixCommand(fallbackMethod = "error")
    public CommonResult<?> getFromParam(String url,Object param) {
        return restTemplate.getForObject(url,CommonResult.class,param);
    }

    @HystrixCommand(fallbackMethod = "error")
    public CommonResult<?> getFrom(String url) {
        return restTemplate.getForObject(url,CommonResult.class);
    }

    @HystrixCommand(fallbackMethod = "error")
    public CommonResult<?> postFromParam(String url,Object param) {
        return restTemplate.postForObject(url,param,CommonResult.class);
    }

    public CommonResult<?> error() {
        return ResponseUtils.fail("sorry,client has error");
    }

    public CommonResult<?> error(String url) {
        return ResponseUtils.fail("sorry," + url + " has error");
    }
}
