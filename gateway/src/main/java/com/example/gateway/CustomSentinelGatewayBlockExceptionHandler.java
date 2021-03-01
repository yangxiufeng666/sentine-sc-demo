package com.example.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.function.Supplier;
import com.alibaba.fastjson.JSON;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Yangxiufeng
 * @date 2020-12-15
 * @time 18:06
 */
public class CustomSentinelGatewayBlockExceptionHandler extends SentinelGatewayBlockExceptionHandler {

    private List<ViewResolver> viewResolvers;
    private List<HttpMessageWriter<?>> messageWriters;

    public CustomSentinelGatewayBlockExceptionHandler(List<ViewResolver> viewResolvers, ServerCodecConfigurer serverCodecConfigurer) {
        super(viewResolvers, serverCodecConfigurer);
        this.viewResolvers = viewResolvers;
        this.messageWriters = serverCodecConfigurer.getWriters();
    }
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {

        if (serverWebExchange.getResponse().isCommitted()) {
            return Mono.error(throwable);
        }
        if (!BlockException.isBlockException(throwable)) {

            String reason=null;
            int status = 0;
            if (throwable instanceof NotFoundException){
                NotFoundException notFoundException = (NotFoundException)throwable;
                reason = notFoundException.getReason();
                status = notFoundException.getStatus().value();
            }else if (throwable instanceof ResponseStatusException){
                ResponseStatusException responseStatusException = (ResponseStatusException)throwable;
                status = responseStatusException.getStatus().value();
                reason = responseStatusException.getMessage();
            }else {
                status = -200;
                reason = throwable.getMessage();
            }
            Map<String , Object> result = new HashMap<>();
            String path = serverWebExchange.getRequest().getURI().getPath();
            result.put("code",status);
            result.put("msg", reason);
            result.put("status", status);
            result.put("path", path);
            return writeResponse(JSON.toJSONString(result), serverWebExchange);
        }
        return handleBlockedRequest(serverWebExchange, throwable).flatMap(response -> writeResponse("{\"code\": 429, \"msg\": \"系统繁忙，请稍后再试(网关限流)\"}", serverWebExchange));
    }

    private Mono<ServerResponse> handleBlockedRequest(ServerWebExchange exchange, Throwable throwable) {
        return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, throwable);
    }
    private Mono<Void> writeResponse(String response, ServerWebExchange exchange) {
        ServerHttpResponse resp = exchange.getResponse();
        resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        DataBuffer buffer = resp.bufferFactory().wrap(response.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Mono.just(buffer));
    }

}
