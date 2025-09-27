package com.zjb.zjbaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.zjb.zjbaicodemother.mapper")
public class ZjbAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZjbAiCodeMotherApplication.class, args);
    }

}
