package com.mishi.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class OrderStateMachineTest {

    private final OrderStateMachine stateMachine = new OrderStateMachine();

    @Test
    void pendingExpiredOrderCanBeClosed() {
        boolean closed = stateMachine.canClose(
                OrderStatus.PENDING_PAYMENT,
                LocalDateTime.now().minusMinutes(16),
                LocalDateTime.now());

        assertThat(closed).isTrue();
    }

    @Test
    void paidOrderCannotBeClosedEvenIfExpired() {
        boolean closed = stateMachine.canClose(
                OrderStatus.PAID,
                LocalDateTime.now().minusMinutes(30),
                LocalDateTime.now());

        assertThat(closed).isFalse();
    }

    @Test
    void recentPendingOrderCannotBeClosed() {
        boolean closed = stateMachine.canClose(
                OrderStatus.PENDING_PAYMENT,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now());

        assertThat(closed).isFalse();
    }
}

