package me.dwywdo.redis.ratelimiter.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<List> rateLimiterScript() {
        final Resource scriptResource = new ClassPathResource("redis-lua-scripts/rate-limiter.lua");
        return RedisScript.of(scriptResource, List.class);
    }
}
