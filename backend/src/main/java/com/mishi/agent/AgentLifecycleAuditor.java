package com.mishi.agent;

import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AgentLifecycleAuditor {
    private static final Logger log = LoggerFactory.getLogger(AgentLifecycleAuditor.class);

    public Instant onAgent(AgentQuestion question) {
        log.info("onAgent userId={} sessionId={}", question.userId(), question.sessionId());
        return Instant.now();
    }

    public void onReasoning(String intent) { log.info("onReasoning intent={}", intent); }
    public void onActing(String toolName) { log.info("onActing tool={}", toolName); }
    public void onModelCall(String modelName, int estimatedTokens) { log.info("onModelCall model={} estimatedTokens={}", modelName, estimatedTokens); }
    public long elapsedMs(Instant start) { return Duration.between(start, Instant.now()).toMillis(); }
}
