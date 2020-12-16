package com.example.consumer.demo.facade.fallback;

import com.example.consumer.demo.facade.ProviderFacade;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProviderFacadeFallbackFactory implements FallbackFactory<ProviderFacade> {
    @Override
    public ProviderFacade create(Throwable throwable) {
        log.error("ProviderFacadeFallbackFactory",throwable);
        return new ProviderFacade() {
            @Override
            public String getPort() {
                return "error";
            }
        };
    }
}
