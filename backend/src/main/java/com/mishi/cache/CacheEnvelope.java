package com.mishi.cache;

import java.time.LocalDateTime;

public record CacheEnvelope<T>(T data, LocalDateTime logicalExpireAt, boolean emptyValue) {
    public boolean logicallyExpired(LocalDateTime now) { return logicalExpireAt != null && logicalExpireAt.isBefore(now); }
    public static <T> CacheEnvelope<T> value(T data, LocalDateTime expireAt) { return new CacheEnvelope<>(data, expireAt, false); }
    public static <T> CacheEnvelope<T> empty(LocalDateTime expireAt) { return new CacheEnvelope<>(null, expireAt, true); }
}
