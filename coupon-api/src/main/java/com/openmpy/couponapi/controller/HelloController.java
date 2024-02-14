package com.openmpy.couponapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        // 초당 2건을 처리 * N (서버에서 동시에 처리할 수 있는 수)
        Thread.sleep(500);
        return "hello!";
    }
}
