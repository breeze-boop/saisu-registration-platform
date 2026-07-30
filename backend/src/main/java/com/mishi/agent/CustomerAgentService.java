package com.mishi.agent;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CustomerAgentService {
    private static final List<String> AVAILABLE_TOOLS = List.of("query_order", "check_inventory", "validate_coupon", "track_logistics", "apply_refund");
    private static final String FALLBACK_MODEL = "local-deterministic-react-fallback";

    private final ECommerceAgentTools tools;
    private final AgentLifecycleAuditor auditor;
    private final LlmAgentClient llmClient;

    public CustomerAgentService(ECommerceAgentTools tools, AgentLifecycleAuditor auditor, LlmAgentClient llmClient) {
        this.tools = tools;
        this.auditor = auditor;
        this.llmClient = llmClient;
    }

    public AgentReply chat(AgentQuestion question) {
        Instant start = auditor.onAgent(question);
        int estimatedTokens = Math.max(8, question.question().length() / 2);
        if (llmClient.isConfigured()) {
            auditor.onModelCall(llmClient.modelName(), estimatedTokens);
            Optional<AgentToolDecision> decision = llmClient.decide(question, AVAILABLE_TOOLS);
            if (decision.isPresent()) {
                auditor.onReasoning("llm:" + decision.get().reason());
                return executeDecision(question, decision.get(), start, estimatedTokens, llmClient.modelName());
            }
        }
        return fallback(question, start, estimatedTokens);
    }

    private AgentReply fallback(AgentQuestion question, Instant start, int estimatedTokens) {
        String text = question.question().toLowerCase(Locale.ROOT);
        auditor.onModelCall(FALLBACK_MODEL, estimatedTokens);
        if (containsAny(text, "退款", "退钱", "refund")) {
            auditor.onReasoning("fallback:refund");
            return executeDecision(question, new AgentToolDecision("apply_refund", 1L, 10001L, "用户咨询退款"), start, estimatedTokens, FALLBACK_MODEL);
        }
        if (containsAny(text, "物流", "快递", "配送", "tracking")) {
            auditor.onReasoning("fallback:logistics");
            return executeDecision(question, new AgentToolDecision("track_logistics", 1L, 10001L, "用户咨询物流"), start, estimatedTokens, FALLBACK_MODEL);
        }
        if (containsAny(text, "库存", "还有", "抢", "秒杀", "stock")) {
            auditor.onReasoning("fallback:inventory");
            return executeDecision(question, new AgentToolDecision("check_inventory", 1L, 10001L, "用户咨询库存"), start, estimatedTokens, FALLBACK_MODEL);
        }
        if (containsAny(text, "优惠券", "券", "coupon", "可用")) {
            auditor.onReasoning("fallback:coupon");
            return executeDecision(question, new AgentToolDecision("validate_coupon", 1L, 10001L, "用户咨询优惠券"), start, estimatedTokens, FALLBACK_MODEL);
        }
        auditor.onReasoning("fallback:order");
        return executeDecision(question, new AgentToolDecision("query_order", 1L, 10001L, "用户咨询订单"), start, estimatedTokens, FALLBACK_MODEL);
    }

    private AgentReply executeDecision(AgentQuestion question, AgentToolDecision decision, Instant start, int estimatedTokens, String model) {
        String toolName = decision.toolName();
        auditor.onActing(toolName);
        String observation = switch (toolName) {
            case "apply_refund" -> tools.applyRefund(question.userId(), defaultOrderId(decision), decision.reason());
            case "track_logistics" -> tools.trackLogistics(defaultOrderId(decision));
            case "check_inventory" -> tools.checkInventory(defaultVoucherId(decision));
            case "validate_coupon" -> tools.validateCoupon(defaultVoucherId(decision), question.userId());
            case "query_order" -> tools.queryOrder(question.userId(), defaultOrderId(decision));
            default -> tools.queryOrder(question.userId(), 10001L);
        };
        return new AgentReply(toAnswer(observation), List.of(toolName), auditor.elapsedMs(start), estimatedTokens, model);
    }

    private Long defaultVoucherId(AgentToolDecision decision) { return decision.voucherId() == null ? 1L : decision.voucherId(); }
    private Long defaultOrderId(AgentToolDecision decision) { return decision.orderId() == null ? 10001L : decision.orderId(); }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) if (text.contains(keyword)) return true;
        return false;
    }

    private String toAnswer(String observation) { return "我已调用后端业务工具查询：" + observation; }
}
