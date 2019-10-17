package com.lingxiao.config;

import com.lingxiao.common.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IdWorkerProperties.class)
public class IdWorkerConfig {
    @Autowired
    private IdWorkerProperties properties;
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(properties.getWorkerId(),properties.getDatacenterId());
    }
}
