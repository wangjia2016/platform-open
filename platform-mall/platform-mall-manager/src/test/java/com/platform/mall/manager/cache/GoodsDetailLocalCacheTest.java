package com.platform.mall.manager.cache;

import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class GoodsDetailLocalCacheTest {

    @Test
    void hitWithinTtlAndMissAfterExpire() throws InterruptedException {
        GoodsDetailLocalCache cache = new GoodsDetailLocalCache(8, 20_000_000L);
        MallGoodsDetailDto detail = MallGoodsDetailDto.builder().id(7L).name("sku").build();
        cache.put(7L, detail);
        assertSame(detail, cache.get(7L));
        Thread.sleep(30L);
        assertNull(cache.get(7L));
    }

    @Test
    void invalidateDropsEntry() {
        GoodsDetailLocalCache cache = new GoodsDetailLocalCache(8);
        cache.put(3L, MallGoodsDetailDto.builder().id(3L).build());
        cache.invalidate(3L);
        assertNull(cache.get(3L));
    }

    @Test
    void fullCacheDoesNotGrowForNewKeys() {
        GoodsDetailLocalCache cache = new GoodsDetailLocalCache(1, GoodsDetailLocalCache.DEFAULT_TTL_NANOS);
        cache.put(1L, MallGoodsDetailDto.builder().id(1L).build());
        cache.put(2L, MallGoodsDetailDto.builder().id(2L).build());
        assertSame(1L, cache.get(1L).getId());
        assertNull(cache.get(2L));
    }
}
