package com.example.consumer.demo.facade.fallback;

import com.example.consumer.demo.facade.ProviderFacade;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Mr.Yangxiufeng
 * Date: 2019-07-25
 * Time: 16:38
 */
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
