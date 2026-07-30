package com.mishi.seckill;

import com.mishi.dto.SeckillSubmitResponse;

public interface SeckillService {
    SeckillSubmitResponse submit(Long voucherId, Long userId);
    void preloadVoucher(Long voucherId);
}
