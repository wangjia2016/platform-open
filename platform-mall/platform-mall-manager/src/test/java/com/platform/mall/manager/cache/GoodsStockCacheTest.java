package com.platform.mall.manager.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoodsStockCacheTest {

    @Test
    void deductScriptIsAtomicDecrement() {
        assertTrue(GoodsStockCache.DEDUCT_LUA.contains("DECRBY"));
        assertTrue(GoodsStockCache.DEDUCT_LUA.contains("stock < num"));
        assertTrue(GoodsStockCache.DEDUCT_LUA.contains("EXPIRE"));
        assertTrue(GoodsStockCache.RESTORE_LUA.contains("EXISTS"));
        assertTrue(GoodsStockCache.RESTORE_LUA.contains("INCRBY"));
    }

    @Test
    void applyMatchesLuaReturnCodes() {
        assertEquals(8L, GoodsStockCache.apply(10, 2));
        assertEquals(-1L, GoodsStockCache.apply(1, 2));
        assertEquals(-2L, GoodsStockCache.apply(null, 1));
        assertEquals(-3L, GoodsStockCache.apply(5, 0));
    }
}
