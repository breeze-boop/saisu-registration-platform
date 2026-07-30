package com.mishi.mq;

public record CacheInvalidationMessage(String type, Long id, int attempts) {}
