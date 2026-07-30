package com.mishi.cache;

import com.mishi.dto.ShopDto;
import java.util.List;

public interface ShopQueryService {
    List<ShopDto> listShops();
    ShopDto getShop(Long id);
    void invalidateShop(Long id);
}
