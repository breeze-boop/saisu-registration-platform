package com.mishi.agent;

import java.util.List;

public record AgentReply(String answer, List<String> usedTools, long elapsedMs, int estimatedTokens, String model) {}
