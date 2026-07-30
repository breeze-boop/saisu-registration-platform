package com.mishi.service;

import com.mishi.cache.CacheInvalidationPublisher;
import com.mishi.cache.ShopQueryService;
import com.mishi.common.BusinessException;
import com.mishi.domain.Shop;
import com.mishi.dto.ShopDto;
import com.mishi.dto.ShopUpdateRequest;
import com.mishi.mapper.ShopMapper;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopCommandService {
    private final ShopMapper shopMapper;
    private final ShopQueryService shopQueryService;
    private final CacheInvalidationPublisher invalidationPublisher;

    public ShopCommandService(ShopMapper shopMapper, ShopQueryService shopQueryService, CacheInvalidationPublisher invalidationPublisher) {
        this.shopMapper = shopMapper;
        this.shopQueryService = shopQueryService;
        this.invalidationPublisher = invalidationPublisher;
    }

    @Transactional
    public ShopDto update(Long id, ShopUpdateRequest request) {
        Shop existing = shopMapper.selectById(id);
        if (existing == null) throw new BusinessException("商家不存在");
        existing.setName(request.name());
        existing.setCategory(request.category());
        existing.setAddress(request.address());
        existing.setAvgPrice(request.avgPrice());
        existing.setScore(request.score());
        existing.setDescription(request.description());
        existing.setUpdatedAt(LocalDateTime.now());
        shopMapper.updateById(existing);
        try {
            shopQueryService.invalidateShop(id);
        } catch (RuntimeException ex) {
            invalidationPublisher.publishShopInvalidation(id);
        }
        return shopQueryService.getShop(id);
    }
}
