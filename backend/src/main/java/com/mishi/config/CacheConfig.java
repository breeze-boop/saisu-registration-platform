package com.mishi.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mishi.cache.CacheEnvelope;
import com.mishi.dto.ShopDto;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    @Bean
    Cache<Long, CacheEnvelope<ShopDto>> shopLocalCache() {
        return Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(Duration.ofMinutes(5)).recordStats().build();
    }
}
