package com.zjb.zjbaicodemother;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class ZjbAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZjbAiCodeMotherApplication.class, args);
    }

}
