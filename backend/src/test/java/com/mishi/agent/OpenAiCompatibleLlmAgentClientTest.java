package com.mishi.agent;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class OpenAiCompatibleLlmAgentClientTest {
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    void callsOpenAiCompatibleEndpointAndParsesToolDecisionWhenApiKeyExists() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/chat/completions", exchange -> {
            assertThat(exchange.getRequestHeaders().getFirst("Authorization")).isEqualTo("Bearer test-key");
            String response = """
                    {"choices":[{"message":{"content":"{\\\"toolName\\\":\\\"check_inventory\\\",\\\"voucherId\\\":2,\\\"orderId\\\":10001,\\\"reason\\\":\\\"用户询问库存\\\"}"}}]}
                    """;
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream body = exchange.getResponseBody()) { body.write(bytes); }
        });
        server.start();
        String baseUrl = "http://localhost:" + server.getAddress().getPort();
        OpenAiCompatibleLlmAgentClient client = new OpenAiCompatibleLlmAgentClient(
                new LlmProperties("test-key", baseUrl, "test-model", 5),
                new ObjectMapper());

        Optional<AgentToolDecision> decision = client.decide(
                new AgentQuestion(7L, "s1", "优惠券2还有库存吗"),
                List.of("query_order", "check_inventory", "validate_coupon", "track_logistics", "apply_refund"));

        assertThat(decision).isPresent();
        assertThat(decision.get().toolName()).isEqualTo("check_inventory");
        assertThat(decision.get().voucherId()).isEqualTo(2L);
    }

    @Test
    void doesNotCallModelWhenApiKeyIsBlank() {
        OpenAiCompatibleLlmAgentClient client = new OpenAiCompatibleLlmAgentClient(
                new LlmProperties("", "http://localhost:1", "test-model", 1),
                new ObjectMapper());

        Optional<AgentToolDecision> decision = client.decide(
                new AgentQuestion(7L, "s1", "我要退款"),
                List.of("query_order", "check_inventory", "validate_coupon", "track_logistics", "apply_refund"));

        assertThat(client.isConfigured()).isFalse();
        assertThat(decision).isEmpty();
    }
}
