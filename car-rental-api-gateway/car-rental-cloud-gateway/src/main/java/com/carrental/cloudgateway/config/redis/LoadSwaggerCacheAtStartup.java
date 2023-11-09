package com.carrental.cloudgateway.config.redis;

import com.carrental.cloudgateway.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "swagger-validator", name = "enabled", havingValue = "true")
public class LoadSwaggerCacheAtStartup {

    private final RedisService redisService;

    @EventListener(ApplicationStartedEvent.class)
    public void loadSwaggerFolderCache() {
        redisService.addSwaggerFolderToRedis()
                .subscribe();
    }

}
