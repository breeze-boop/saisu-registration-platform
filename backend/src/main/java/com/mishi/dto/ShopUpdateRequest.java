package com.mishi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ShopUpdateRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String address,
        @NotNull BigDecimal avgPrice,
        @NotNull BigDecimal score,
        @NotBlank String description) {}
