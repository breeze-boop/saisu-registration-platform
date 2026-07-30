package com.mishi.agent;

public record AgentToolDecision(String toolName, Long voucherId, Long orderId, String reason) {
    public AgentToolDecision {
        if (toolName != null) toolName = toolName.trim();
    }
}
