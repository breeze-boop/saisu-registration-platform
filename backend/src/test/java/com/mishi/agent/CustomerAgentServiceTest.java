package com.mishi.agent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CustomerAgentServiceTest {

    private final ECommerceAgentTools tools = new ECommerceAgentTools(new StubAgentBusinessGateway());
    private final AgentLifecycleAuditor auditor = new AgentLifecycleAuditor();

    @Test
    void fallsBackToLocalRouterWhenNoLlmApiKeyIsConfigured() {
        CustomerAgentService agentService = new CustomerAgentService(tools, auditor, new DisabledLlmAgentClient());

        AgentReply reply = agentService.chat(new AgentQuestion(7L, "s1", "这个优惠券还有库存吗？"));

        assertThat(reply.usedTools()).containsExactly("check_inventory");
        assertThat(reply.answer()).contains("库存");
        assertThat(reply.model()).isEqualTo("local-deterministic-react-fallback");
    }

    @Test
    void usesConfiguredLlmDecisionBeforeFallbackRouting() {
        CustomerAgentService agentService = new CustomerAgentService(
                tools,
                auditor,
                new FakeLlmAgentClient(new AgentToolDecision("apply_refund", 2L, 90002L, "模型判断用户需要退款")));

        AgentReply reply = agentService.chat(new AgentQuestion(7L, "s1", "帮我处理一下售后问题"));

        assertThat(reply.usedTools()).containsExactly("apply_refund");
        assertThat(reply.answer()).contains("退款");
        assertThat(reply.model()).isEqualTo("test-llm");
    }

    @Test
    void fallsBackWhenConfiguredLlmReturnsNoDecision() {
        CustomerAgentService agentService = new CustomerAgentService(tools, auditor, new FakeLlmAgentClient(null));

        AgentReply reply = agentService.chat(new AgentQuestion(7L, "s1", "我的物流到哪里了"));

        assertThat(reply.usedTools()).containsExactly("track_logistics");
        assertThat(reply.answer()).contains("物流");
        assertThat(reply.model()).isEqualTo("local-deterministic-react-fallback");
    }

    private record FakeLlmAgentClient(AgentToolDecision decision) implements LlmAgentClient {
        @Override public boolean isConfigured() { return true; }
        @Override public String modelName() { return "test-llm"; }
        @Override public Optional<AgentToolDecision> decide(AgentQuestion question, List<String> availableTools) {
            return Optional.ofNullable(decision);
        }
    }
}
