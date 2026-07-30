package com.mishi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherDto(Long id, Long shopId, String title, BigDecimal payValue, BigDecimal actualValue, Integer stock, LocalDateTime beginAt, LocalDateTime endAt) {}
