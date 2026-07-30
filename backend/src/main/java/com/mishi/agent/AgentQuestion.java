package com.mishi.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgentQuestion(@NotNull Long userId, @NotBlank String sessionId, @NotBlank String question) {}
