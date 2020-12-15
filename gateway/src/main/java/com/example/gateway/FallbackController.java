package com.example.gateway;

import com.netflix.hystrix.exception.HystrixTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Yangxiufeng
 * @date 2020-09-14
 * @time 11:00
 */
@RestController
@Slf4j
public class FallbackController {
    @RequestMapping(value = "/fallback")
    @ResponseStatus
    public Mono<Map<String, Object>> fallback(ServerWebExchange exchange) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("code", 60002);
        result.put("data", null);
        Exception exception = exchange.getAttribute(ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR);
        ServerWebExchange delegate = ((ServerWebExchangeDecorator) exchange).getDelegate();
        log.error("接口调用失败，URL={}", delegate.getRequest().getURI(), exception);
        if (exception instanceof HystrixTimeoutException) {
            result.put("msg", "接口调用超时");
        } else if (exception != null && exception.getMessage() != null) {
            result.put("msg", "接口调用失败: " + exception.getMessage());
        } else {
            result.put("msg", "接口调用失败");
        }
        return Mono.just(result);
    }
}
