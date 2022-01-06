package com.cloud.transport;

import com.cloud.common.CommonResult;
import com.cloud.utils.ResponseUtils;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author hongze
 * @date
 * @apiNote
 *   More about Hystrix command properties please visit
 *   https://github.com/Netflix/Hystrix/wiki/Configuration#CommandExecution
 */
@Component
@DefaultProperties(groupKey = "DefaultGroupKey", defaultFallback = "error",
        commandProperties = {
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")
        },
        threadPoolProperties = {
                @HystrixProperty(name = "coreSize", value = "30"),
                @HystrixProperty(name = "maxQueueSize", value = "100"),
                @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
                @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
                @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")
        })
public class RestTransport {
    @Resource
    private RestTemplate restTemplate;

    @HystrixCommand()
    public CommonResult<?> getFromParam(String url,Object param) {
        return restTemplate.getForObject(url,CommonResult.class,param);
    }

    @HystrixCommand()
    public CommonResult<?> getFrom(String url) {
        return restTemplate.getForObject(url,CommonResult.class);
    }

    @HystrixCommand()
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
