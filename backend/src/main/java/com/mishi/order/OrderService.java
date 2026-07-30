package com.mishi.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mishi.common.BusinessException;
import com.mishi.domain.VoucherOrder;
import com.mishi.dto.OrderDto;
import com.mishi.mapper.VoucherMapper;
import com.mishi.mapper.VoucherOrderMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final VoucherOrderMapper orderMapper;
    private final VoucherMapper voucherMapper;
    private final OrderStateMachine stateMachine;

    public OrderService(VoucherOrderMapper orderMapper, VoucherMapper voucherMapper, OrderStateMachine stateMachine) {
        this.orderMapper = orderMapper;
        this.voucherMapper = voucherMapper;
        this.stateMachine = stateMachine;
    }

    public OrderDto getOrder(Long id) {
        VoucherOrder order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("订单不存在");
        return toDto(order);
    }

    public OrderDto pay(Long id) {
        int updated = orderMapper.payPendingOrder(id, LocalDateTime.now());
        if (updated == 0) throw new BusinessException("订单不可支付或已关闭");
        return getOrder(id);
    }

    @Scheduled(fixedDelayString = "${mishi.order.timeout-scan-delay-ms:30000}")
    public void closeExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<VoucherOrder> pending = orderMapper.selectList(new LambdaQueryWrapper<VoucherOrder>().eq(VoucherOrder::getStatus, OrderStatus.PENDING_PAYMENT.name()));
        for (VoucherOrder order : pending) {
            if (stateMachine.canClose(OrderStatus.valueOf(order.getStatus()), order.getCreatedAt(), now)) {
                int closed = orderMapper.closePendingOrder(order.getId(), now);
                if (closed > 0) voucherMapper.releaseStock(order.getVoucherId());
            }
        }
    }

    public OrderDto toDto(VoucherOrder order) {
        return new OrderDto(order.getId(), order.getVoucherId(), order.getUserId(), order.getStatus(), order.getCreatedAt(), order.getPaidAt(), order.getClosedAt());
    }
}
