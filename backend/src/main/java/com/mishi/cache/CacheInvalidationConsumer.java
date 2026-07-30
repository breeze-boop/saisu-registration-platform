package com.mishi.cache;

import com.mishi.config.RabbitConfig;
import com.mishi.mq.CacheInvalidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationConsumer {
    private static final Logger log = LoggerFactory.getLogger(CacheInvalidationConsumer.class);
    private final ShopQueryService shopQueryService;
    private final CacheInvalidationPublisher publisher;

    public CacheInvalidationConsumer(ShopQueryService shopQueryService, CacheInvalidationPublisher publisher) {
        this.shopQueryService = shopQueryService;
        this.publisher = publisher;
    }

    @RabbitListener(queues = RabbitConfig.CACHE_QUEUE)
    public void consume(CacheInvalidationMessage message) {
        try {
            if ("SHOP".equals(message.type())) shopQueryService.invalidateShop(message.id());
        } catch (RuntimeException ex) {
            if (message.attempts() < 3) {
                publisher.publishShopInvalidation(message.id());
            } else {
                log.error("cache invalidation exhausted type={} id={}", message.type(), message.id(), ex);
            }
        }
    }
}
