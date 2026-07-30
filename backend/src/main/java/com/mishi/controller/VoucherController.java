package com.mishi.controller;

import com.mishi.common.ApiResponse;
import com.mishi.dto.VoucherDto;
import com.mishi.seckill.SeckillService;
import com.mishi.service.VoucherQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    private final VoucherQueryService voucherQueryService;
    private final SeckillService seckillService;
    public VoucherController(VoucherQueryService voucherQueryService, SeckillService seckillService) { this.voucherQueryService = voucherQueryService; this.seckillService = seckillService; }
    @GetMapping public ApiResponse<List<VoucherDto>> list() { return ApiResponse.ok(voucherQueryService.list()); }
    @GetMapping("/{id}") public ApiResponse<VoucherDto> get(@PathVariable Long id) { return ApiResponse.ok(voucherQueryService.get(id)); }
    @PostMapping("/{id}/preload") public ApiResponse<Void> preload(@PathVariable Long id) { seckillService.preloadVoucher(id); return ApiResponse.ok(null); }
}
