package com.mishi.seckill;

public final class RedisSeckillKeys {
    private RedisSeckillKeys() {}
    public static String stock(Long voucherId) { return "seckill:stock:" + voucherId; }
    public static String order(Long voucherId) { return "seckill:order:" + voucherId; }
}
