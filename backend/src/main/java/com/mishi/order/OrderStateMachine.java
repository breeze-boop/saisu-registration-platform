package com.mishi.order;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class OrderStateMachine {
    private static final Duration PAYMENT_TIMEOUT = Duration.ofMinutes(15);

    public boolean canClose(OrderStatus status, LocalDateTime createdAt, LocalDateTime now) {
        return status == OrderStatus.PENDING_PAYMENT && createdAt.plus(PAYMENT_TIMEOUT).isBefore(now);
    }
}
