package com.mishi.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiCompatibleLlmAgentClient implements LlmAgentClient {
    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleLlmAgentClient.class);
    private static final Pattern TOOL_PATTERN = Pattern.compile("(?i)(query_order|check_inventory|validate_coupon|track_logistics|apply_refund)");
    private static final Pattern VOUCHER_PATTERN = Pattern.compile("(?i)voucherId\\D+(\\d+)|优惠券\\D*(\\d+)");
    private static final Pattern ORDER_PATTERN = Pattern.compile("(?i)orderId\\D+(\\d+)|订单\\D*(\\d+)");

    private final LlmProperties properties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public OpenAiCompatibleLlmAgentClient(LlmProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(properties.timeoutSeconds()));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public boolean isConfigured() { return properties.hasApiKey(); }

    @Override
    public String modelName() { return properties.model(); }

    @Override
    public Optional<AgentToolDecision> decide(AgentQuestion question, List<String> availableTools) {
        if (!isConfigured()) return Optional.empty();
        try {
            Map<String, Object> body = Map.of(
                    "model", properties.model(),
                    "temperature", 0,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt(availableTools)),
                            Map.of("role", "user", "content", question.question())));
            String response = restClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey())
                    .body(body)
                    .retrieve()
                    .body(String.class);
            if (response == null || response.isBlank()) return Optional.empty();
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            return parseDecision(content, question, availableTools);
        } catch (RuntimeException ex) {
            log.warn("LLM decision failed, fallback router will be used: {}", ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("LLM decision parse failed, fallback router will be used: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private String systemPrompt(List<String> availableTools) {
        return "你是觅食电商客服的工具路由 Agent。只能从这些工具中选择一个：" + String.join(",", availableTools) + "。"
                + "请只输出 JSON，例如 {\"toolName\":\"check_inventory\",\"voucherId\":1,\"orderId\":10001,\"reason\":\"用户询问库存\"}。"
                + "不要直接回答用户，业务结果由后端工具返回。";
    }

    private Optional<AgentToolDecision> parseDecision(String content, AgentQuestion question, List<String> availableTools) {
        if (content == null || content.isBlank()) return Optional.empty();
        Optional<AgentToolDecision> jsonDecision = parseJsonDecision(content, availableTools);
        if (jsonDecision.isPresent()) return jsonDecision;
        Matcher matcher = TOOL_PATTERN.matcher(content);
        if (matcher.find() && availableTools.contains(matcher.group(1))) {
            return Optional.of(new AgentToolDecision(matcher.group(1), extractLong(content, VOUCHER_PATTERN).orElse(1L), extractLong(content, ORDER_PATTERN).orElse(10001L), "模型文本路由"));
        }
        return Optional.empty();
    }

    private Optional<AgentToolDecision> parseJsonDecision(String content, List<String> availableTools) {
        try {
            int start = content.indexOf('{');
            int end = content.lastIndexOf('}');
            if (start < 0 || end <= start) return Optional.empty();
            JsonNode node = objectMapper.readTree(content.substring(start, end + 1));
            String toolName = node.path("toolName").asText("");
            if (!availableTools.contains(toolName)) return Optional.empty();
            Long voucherId = node.hasNonNull("voucherId") ? node.path("voucherId").asLong() : 1L;
            Long orderId = node.hasNonNull("orderId") ? node.path("orderId").asLong() : 10001L;
            String reason = node.path("reason").asText("模型 JSON 路由");
            return Optional.of(new AgentToolDecision(toolName, voucherId, orderId, reason));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private Optional<Long> extractLong(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) return Optional.empty();
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (matcher.group(i) != null) return Optional.of(Long.parseLong(matcher.group(i)));
        }
        return Optional.empty();
    }
}
