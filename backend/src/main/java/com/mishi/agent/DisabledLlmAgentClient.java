package com.mishi.agent;

import java.util.List;
import java.util.Optional;

public class DisabledLlmAgentClient implements LlmAgentClient {
    @Override public boolean isConfigured() { return false; }
    @Override public String modelName() { return "local-deterministic-react-fallback"; }
    @Override public Optional<AgentToolDecision> decide(AgentQuestion question, List<String> availableTools) { return Optional.empty(); }
}
