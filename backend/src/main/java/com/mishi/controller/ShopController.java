package com.mishi.controller;

import com.mishi.cache.ShopQueryService;
import com.mishi.common.ApiResponse;
import com.mishi.dto.ShopDto;
import com.mishi.dto.ShopUpdateRequest;
import com.mishi.service.ShopCommandService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    private final ShopQueryService shopQueryService;
    private final ShopCommandService shopCommandService;
    public ShopController(ShopQueryService shopQueryService, ShopCommandService shopCommandService) { this.shopQueryService = shopQueryService; this.shopCommandService = shopCommandService; }
    @GetMapping public ApiResponse<List<ShopDto>> list() { return ApiResponse.ok(shopQueryService.listShops()); }
    @GetMapping("/{id}") public ApiResponse<ShopDto> get(@PathVariable Long id) { return ApiResponse.ok(shopQueryService.getShop(id)); }
    @org.springframework.web.bind.annotation.PutMapping("/{id}") public ApiResponse<ShopDto> update(@PathVariable Long id, @Valid @org.springframework.web.bind.annotation.RequestBody ShopUpdateRequest request) { return ApiResponse.ok(shopCommandService.update(id, request)); }
}

