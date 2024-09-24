package com.privatbank.test_task.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.profiles.active}")
    private String environment;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+host+":"+port);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean
    @DependsOn({"redisson"})
    public RedisConnectionFactory redisConnectionFactory(
            RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    @Bean
    @DependsOn({"redisConnectionFactory"})
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory, environment);
    }
}