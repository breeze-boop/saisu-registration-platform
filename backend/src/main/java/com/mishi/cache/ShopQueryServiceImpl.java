package com.mishi.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.mishi.common.BusinessException;
import com.mishi.domain.Shop;
import com.mishi.dto.ShopDto;
import com.mishi.mapper.ShopMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopQueryServiceImpl implements ShopQueryService {
    private static final String SHOP_KEY = "cache:shop:";
    private final ShopMapper shopMapper;
    private final StringRedisTemplate redisTemplate;
    private final Cache<Long, CacheEnvelope<ShopDto>> localCache;
    private final ObjectMapper objectMapper;
    private final SimpleBloomFilter bloomFilter;

    public ShopQueryServiceImpl(ShopMapper shopMapper, StringRedisTemplate redisTemplate, Cache<Long, CacheEnvelope<ShopDto>> localCache, ObjectMapper objectMapper, SimpleBloomFilter bloomFilter) {
        this.shopMapper = shopMapper;
        this.redisTemplate = redisTemplate;
        this.localCache = localCache;
        this.objectMapper = objectMapper;
        this.bloomFilter = bloomFilter;
        listShops().forEach(shop -> bloomFilter.put(String.valueOf(shop.id())));
    }

    @Override
    public List<ShopDto> listShops() {
        return shopMapper.selectList(new LambdaQueryWrapper<Shop>().orderByDesc(Shop::getScore)).stream().map(this::toDto).toList();
    }

    @Override
    public ShopDto getShop(Long id) {
        if (!bloomFilter.mightContain(String.valueOf(id))) throw new BusinessException("商家不存在");
        CacheEnvelope<ShopDto> local = localCache.getIfPresent(id);
        if (local != null && !local.emptyValue() && !local.logicallyExpired(LocalDateTime.now())) return local.data();
        String json = redisTemplate.opsForValue().get(SHOP_KEY + id);
        if (json != null) {
            CacheEnvelope<ShopDto> envelope = readEnvelope(json);
            if (envelope.emptyValue()) throw new BusinessException("商家不存在");
            localCache.put(id, envelope);
            return envelope.data();
        }
        Shop shop = shopMapper.selectById(id);
        if (shop == null) {
            writeEnvelope(id, CacheEnvelope.empty(LocalDateTime.now().plusMinutes(3)), Duration.ofMinutes(3));
            throw new BusinessException("商家不存在");
        }
        ShopDto dto = toDto(shop);
        CacheEnvelope<ShopDto> envelope = CacheEnvelope.value(dto, LocalDateTime.now().plusMinutes(20));
        writeEnvelope(id, envelope, Duration.ofMinutes(30));
        localCache.put(id, envelope);
        return dto;
    }

    @Override
    public void invalidateShop(Long id) {
        localCache.invalidate(id);
        redisTemplate.delete(SHOP_KEY + id);
    }

    private void writeEnvelope(Long id, CacheEnvelope<ShopDto> envelope, Duration ttl) {
        try { redisTemplate.opsForValue().set(SHOP_KEY + id, objectMapper.writeValueAsString(envelope), ttl); }
        catch (JsonProcessingException ex) { throw new BusinessException("缓存序列化失败"); }
    }

    private CacheEnvelope<ShopDto> readEnvelope(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(CacheEnvelope.class, ShopDto.class));
        } catch (JsonProcessingException ex) { throw new BusinessException("缓存反序列化失败"); }
    }

    private ShopDto toDto(Shop shop) {
        return new ShopDto(shop.getId(), shop.getName(), shop.getCategory(), shop.getAddress(), shop.getAvgPrice(), shop.getScore(), shop.getDescription());
    }
}
