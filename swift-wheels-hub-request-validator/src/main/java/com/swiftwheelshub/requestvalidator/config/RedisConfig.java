package com.swiftwheelshub.requestvalidator.config;

import com.swiftwheelshub.requestvalidator.model.SwaggerFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@EnableRedisRepositories(basePackages = "com.swiftwheelshub.requestvalidator")
@PropertySource("classpath:application.yml")
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);

        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder =
                LettuceClientConfiguration.builder();

        LettuceClientConfiguration lettuceClientConfiguration = lettuceClientConfigurationBuilder.build();

        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

    @Bean
    public RedisTemplate<String, SwaggerFile> redisTemplate() {
        RedisTemplate<String, SwaggerFile> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(SwaggerFile.class));

        return redisTemplate;
    }

}
