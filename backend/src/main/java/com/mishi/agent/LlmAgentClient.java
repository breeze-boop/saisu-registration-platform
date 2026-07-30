package com.mishi.agent;

import java.util.List;
import java.util.Optional;

public interface LlmAgentClient {
    boolean isConfigured();
    String modelName();
    Optional<AgentToolDecision> decide(AgentQuestion question, List<String> availableTools);
}
