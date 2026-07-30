package com.mishi.agent;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ECommerceAgentTools {
    private final AgentBusinessGateway gateway;

    public ECommerceAgentTools(AgentBusinessGateway gateway) { this.gateway = gateway; }

    @Tool(name = "query_order", description = "查询用户订单状态、创建时间、支付状态")
    public String queryOrder(@ToolParam(name = "userId", description = "用户ID") Long userId,
                             @ToolParam(name = "orderId", description = "订单ID") Long orderId) {
        return gateway.queryOrder(userId, orderId == null ? 10001L : orderId);
    }

    @Tool(name = "check_inventory", description = "检查秒杀优惠券库存是否充足")
    public String checkInventory(@ToolParam(name = "voucherId", description = "优惠券ID") Long voucherId) {
        return gateway.checkInventory(voucherId == null ? 1L : voucherId);
    }

    @Tool(name = "validate_coupon", description = "校验用户是否可使用指定优惠券")
    public String validateCoupon(@ToolParam(name = "voucherId", description = "优惠券ID") Long voucherId,
                                 @ToolParam(name = "userId", description = "用户ID") Long userId) {
        return gateway.validateCoupon(voucherId == null ? 1L : voucherId, userId);
    }

    @Tool(name = "track_logistics", description = "追踪订单物流状态")
    public String trackLogistics(@ToolParam(name = "orderId", description = "订单ID") Long orderId) {
        return gateway.trackLogistics(orderId == null ? 10001L : orderId);
    }

    @Tool(name = "apply_refund", description = "为用户提交退款申请")
    public String applyRefund(@ToolParam(name = "userId", description = "用户ID") Long userId,
                              @ToolParam(name = "orderId", description = "订单ID") Long orderId,
                              @ToolParam(name = "reason", description = "退款原因") String reason) {
        return gateway.applyRefund(userId, orderId == null ? 10001L : orderId, reason == null ? "用户主动申请" : reason);
    }
}
