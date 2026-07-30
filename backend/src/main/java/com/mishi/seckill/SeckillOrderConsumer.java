package com.mishi.seckill;

import com.mishi.config.RabbitConfig;
import com.mishi.domain.Voucher;
import com.mishi.domain.VoucherOrder;
import com.mishi.mapper.VoucherMapper;
import com.mishi.mapper.VoucherOrderMapper;
import com.mishi.mq.SeckillOrderMessage;
import com.mishi.order.OrderStatus;
import java.time.LocalDateTime;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SeckillOrderConsumer {
    private final VoucherMapper voucherMapper;
    private final VoucherOrderMapper orderMapper;
    private final StringRedisTemplate redisTemplate;

    public SeckillOrderConsumer(VoucherMapper voucherMapper, VoucherOrderMapper orderMapper, StringRedisTemplate redisTemplate) {
        this.voucherMapper = voucherMapper;
        this.orderMapper = orderMapper;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    public void consume(SeckillOrderMessage message) {
        if (orderMapper.countUserVoucherOrders(message.voucherId(), message.userId()) > 0) return;
        Voucher voucher = voucherMapper.selectById(message.voucherId());
        if (voucher == null || voucher.getStock() <= 0) { rollbackRedis(message); return; }
        int deducted = voucherMapper.decreaseStockWithOptimisticLock(message.voucherId(), voucher.getVersion());
        if (deducted == 0) { rollbackRedis(message); return; }
        VoucherOrder order = new VoucherOrder();
        order.setId(message.orderId());
        order.setVoucherId(message.voucherId());
        order.setUserId(message.userId());
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());
        order.setCreatedAt(LocalDateTime.now());
        order.setVersion(0);
        orderMapper.insert(order);
    }

    private void rollbackRedis(SeckillOrderMessage message) {
        redisTemplate.opsForValue().increment(RedisSeckillKeys.stock(message.voucherId()));
        redisTemplate.opsForSet().remove(RedisSeckillKeys.order(message.voucherId()), String.valueOf(message.userId()));
    }
}
