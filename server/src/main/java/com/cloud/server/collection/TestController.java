package com.cloud.server.collection;

import com.cloud.common.CommonResult;
import com.cloud.transport.RestTransport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hongze
 * @date 2021-08-25 10:38:23
 * @apiNote
 */
@RestController
public class TestController {
    @Resource
    private RestTransport restTransport;

    @GetMapping(value = "get")
    public CommonResult<?> hello() {
        return restTransport.getFrom("http://client/hello");
    }
}
