package com.example.provider.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Mr.Yangxiufeng
 * Date: 2019-07-25
 * Time: 14:29
 */
@RestController
public class ProviderController {

    @Value("${server.port}")
    private String port;

    @GetMapping("getPort")
    public String getPort(){
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return port;
    }

}
