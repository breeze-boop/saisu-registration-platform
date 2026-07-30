package com.mishi.cache;

import com.mishi.config.RabbitConfig;
import com.mishi.mq.CacheInvalidationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationPublisher {
    private final RabbitTemplate rabbitTemplate;
    public CacheInvalidationPublisher(RabbitTemplate rabbitTemplate) { this.rabbitTemplate = rabbitTemplate; }
    public void publishShopInvalidation(Long shopId) {
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE, RabbitConfig.CACHE_ROUTING_KEY, new CacheInvalidationMessage("SHOP", shopId, 0));
    }
}
