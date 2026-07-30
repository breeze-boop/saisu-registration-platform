package com.mishi.controller;

import com.mishi.common.ApiResponse;
import com.mishi.dto.SeckillSubmitResponse;
import com.mishi.seckill.SeckillService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {
    private final SeckillService seckillService;
    public SeckillController(SeckillService seckillService) { this.seckillService = seckillService; }
    @PostMapping("/{voucherId}")
    public ApiResponse<SeckillSubmitResponse> submit(@PathVariable Long voucherId, @RequestHeader(value = "X-User-Id", defaultValue = "7") Long userId) {
        return ApiResponse.ok(seckillService.submit(voucherId, userId));
    }
}
