package com.cloud.server.collection;

import com.cloud.common.CommonResult;
import com.cloud.utils.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hongze
 * @date 2021-08-25 10:38:23
 * @apiNote
 */
@RestController
public class TestController {
    @GetMapping(value = "hello")
    public CommonResult<String> hello() {
        return ResponseUtils.ok("hello");
    }
}
