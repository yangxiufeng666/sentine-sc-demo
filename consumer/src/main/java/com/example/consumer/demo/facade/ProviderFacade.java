package com.example.consumer.demo.facade;

import com.example.consumer.demo.facade.fallback.ProviderFacadeFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "sentinel-provider" , fallbackFactory = ProviderFacadeFallbackFactory.class)
public interface ProviderFacade {
    @GetMapping("getPort")
    String getPort();

}
