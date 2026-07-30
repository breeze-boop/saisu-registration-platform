package com.mishi.controller;

import com.mishi.common.ApiResponse;
import com.mishi.dto.OrderDto;
import com.mishi.order.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }
    @GetMapping("/{id}") public ApiResponse<OrderDto> get(@PathVariable Long id) { return ApiResponse.ok(orderService.getOrder(id)); }
    @PostMapping("/{id}/pay") public ApiResponse<OrderDto> pay(@PathVariable Long id) { return ApiResponse.ok(orderService.pay(id)); }
}
