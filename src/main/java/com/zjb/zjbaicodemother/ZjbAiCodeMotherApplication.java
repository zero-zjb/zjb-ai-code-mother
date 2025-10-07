package com.zjb.zjbaicodemother;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.zjb.zjbaicodemother.mapper")
public class ZjbAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZjbAiCodeMotherApplication.class, args);
    }

}
