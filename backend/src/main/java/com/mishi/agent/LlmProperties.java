package com.mishi.agent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LlmProperties {
    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final int timeoutSeconds;

    public LlmProperties(
            @Value("${mishi.llm.api-key:}") String apiKey,
            @Value("${mishi.llm.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${mishi.llm.model:gpt-4o-mini}") String model,
            @Value("${mishi.llm.timeout-seconds:10}") int timeoutSeconds) {
        this.apiKey = apiKey;
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.model = model;
        this.timeoutSeconds = timeoutSeconds;
    }

    public String apiKey() { return apiKey; }
    public String baseUrl() { return baseUrl; }
    public String model() { return model; }
    public int timeoutSeconds() { return timeoutSeconds; }
    public boolean hasApiKey() { return apiKey != null && !apiKey.isBlank(); }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) return "https://api.openai.com/v1";
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
