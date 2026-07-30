package com.mishi.agent;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class StubAgentBusinessGateway implements AgentBusinessGateway {
    @Override public String queryOrder(Long userId, Long orderId) { return "订单" + orderId + "属于用户" + userId + "，当前状态为待支付。"; }
    @Override public String checkInventory(Long voucherId) { return "优惠券" + voucherId + "当前库存充足，可参与秒杀。"; }
    @Override public String validateCoupon(Long voucherId, Long userId) { return "优惠券" + voucherId + "对用户" + userId + "可用，未检测到重复下单。"; }
    @Override public String trackLogistics(Long orderId) { return "订单" + orderId + "物流状态：商家备货中，预计24小时内发出。"; }
    @Override public String applyRefund(Long userId, Long orderId, String reason) { return "已为用户" + userId + "提交订单" + orderId + "的退款申请，原因：" + reason + "。"; }
}
