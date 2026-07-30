package com.mishi.mq;

public record SeckillOrderMessage(Long orderId, Long voucherId, Long userId) {}
