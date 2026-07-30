package com.mishi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String ORDER_EXCHANGE = "mishi.order.exchange";
    public static final String ORDER_QUEUE = "mishi.order.queue";
    public static final String ORDER_ROUTING_KEY = "order.seckill";
    public static final String CACHE_QUEUE = "mishi.cache.invalidate.queue";
    public static final String CACHE_ROUTING_KEY = "cache.invalidate";

    @Bean DirectExchange orderExchange() { return new DirectExchange(ORDER_EXCHANGE, true, false); }
    @Bean Queue orderQueue() { return new Queue(ORDER_QUEUE, true); }
    @Bean Queue cacheInvalidationQueue() { return new Queue(CACHE_QUEUE, true); }
    @Bean Binding orderBinding() { return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ORDER_ROUTING_KEY); }
    @Bean Binding cacheBinding() { return BindingBuilder.bind(cacheInvalidationQueue()).to(orderExchange()).with(CACHE_ROUTING_KEY); }
    @Bean MessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }
}
