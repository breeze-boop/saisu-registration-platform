package com.mishi.dto;

import java.math.BigDecimal;

public record ShopDto(Long id, String name, String category, String address, BigDecimal avgPrice, BigDecimal score, String description) {}
