package com.mishi.seckill;

import com.mishi.common.BusinessException;
import com.mishi.config.RabbitConfig;
import com.mishi.domain.Voucher;
import com.mishi.dto.SeckillSubmitResponse;
import com.mishi.mapper.VoucherMapper;
import com.mishi.mq.SeckillOrderMessage;
import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class SeckillServiceImpl implements SeckillService {
    private final VoucherMapper voucherMapper;
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final DefaultRedisScript<Long> seckillScript;

    public SeckillServiceImpl(VoucherMapper voucherMapper, StringRedisTemplate redisTemplate, RabbitTemplate rabbitTemplate) {
        this.voucherMapper = voucherMapper;
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.seckillScript = new DefaultRedisScript<>();
        this.seckillScript.setResultType(Long.class);
        this.seckillScript.setLocation(new ClassPathResource("lua/seckill.lua"));
    }

    @Override
    public SeckillSubmitResponse submit(Long voucherId, Long userId) {
        Voucher voucher = voucherMapper.selectById(voucherId);
        if (voucher == null) throw new BusinessException("优惠券不存在");
        Long orderId = System.currentTimeMillis() * 1000 + userId % 1000;
        Long result = redisTemplate.execute(seckillScript, List.of(RedisSeckillKeys.stock(voucherId), RedisSeckillKeys.order(voucherId)), String.valueOf(userId));
        if (result == null) throw new BusinessException("秒杀系统繁忙");
        if (result == 1L) throw new BusinessException("库存不足");
        if (result == 2L) throw new BusinessException("同一用户只能下一单");
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE, RabbitConfig.ORDER_ROUTING_KEY, new SeckillOrderMessage(orderId, voucherId, userId));
        return new SeckillSubmitResponse(orderId, "QUEUED", "秒杀请求已进入异步下单队列");
    }

    @Override
    public void preloadVoucher(Long voucherId) {
        Voucher voucher = voucherMapper.selectById(voucherId);
        if (voucher == null) throw new BusinessException("优惠券不存在");
        redisTemplate.opsForValue().set(RedisSeckillKeys.stock(voucherId), String.valueOf(voucher.getStock()));
    }
}
