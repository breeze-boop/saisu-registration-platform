package com.mishi.dto;

import java.time.LocalDateTime;

public record OrderDto(Long id, Long voucherId, Long userId, String status, LocalDateTime createdAt, LocalDateTime paidAt, LocalDateTime closedAt) {}
