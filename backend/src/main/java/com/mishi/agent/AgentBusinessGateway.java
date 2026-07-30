package com.mishi.agent;

public interface AgentBusinessGateway {
    String queryOrder(Long userId, Long orderId);
    String checkInventory(Long voucherId);
    String validateCoupon(Long voucherId, Long userId);
    String trackLogistics(Long orderId);
    String applyRefund(Long userId, Long orderId, String reason);
}
