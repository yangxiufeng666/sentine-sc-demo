package com.example.consumer.demo.controller;

import com.example.consumer.demo.facade.ProviderFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {
    @Autowired
    private ProviderFacade providerFacade;

    @GetMapping("getProviderPort")
    public String getProviderPort(){
//        return restTemplate.getForObject("http://sentinel-provider/getPort", String.class);
        return providerFacade.getPort();
    }

}
