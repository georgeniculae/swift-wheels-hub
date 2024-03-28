package com.swiftwheelshub.requestvalidator.redis;

import com.swiftwheelshub.requestvalidator.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadSwaggerCacheAtStartup implements RetryListener {

    private final RedisService redisService;

    @EventListener(ApplicationStartedEvent.class)
    public void loadSwaggerFilesCache() {
        redisService.addSwaggerFilesToRedis();
    }

}
