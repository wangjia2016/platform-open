package com.platform.mall.manager.cache;

import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 商品详情的进程内短缓存。库存不放这里，仍由 Redis 计数。
 * 调用方只读返回对象，不要改字段。
 */
public class GoodsDetailLocalCache {

    static final long DEFAULT_TTL_NANOS = TimeUnit.SECONDS.toNanos(5);

    private final int maxSize;
    private final long ttlNanos;
    private final ConcurrentHashMap<Long, Entry> entries = new ConcurrentHashMap<>();

    public GoodsDetailLocalCache(int maxSize) {
        this(maxSize, DEFAULT_TTL_NANOS);
    }

    GoodsDetailLocalCache(int maxSize, long ttlNanos) {
        this.maxSize = maxSize;
        this.ttlNanos = ttlNanos;
    }

    public MallGoodsDetailDto get(Long id) {
        if (id == null) {
            return null;
        }
        Entry entry = entries.get(id);
        if (entry == null) {
            return null;
        }
        if (entry.expireAt < System.nanoTime()) {
            entries.remove(id, entry);
            return null;
        }
        return entry.detail;
    }

    public void put(Long id, MallGoodsDetailDto detail) {
        if (id == null || detail == null) {
            return;
        }
        if (entries.size() >= maxSize && entries.get(id) == null) {
            evictExpired();
        }
        if (entries.size() >= maxSize && entries.get(id) == null) {
            return;
        }
        entries.put(id, new Entry(detail, System.nanoTime() + ttlNanos));
    }

    public void invalidate(Long id) {
        if (id != null) {
            entries.remove(id);
        }
    }

    int size() {
        return entries.size();
    }

    private void evictExpired() {
        long now = System.nanoTime();
        entries.entrySet().removeIf(item -> item.getValue().expireAt < now);
    }

    private static final class Entry {
        private final MallGoodsDetailDto detail;
        private final long expireAt;

        private Entry(MallGoodsDetailDto detail, long expireAt) {
            this.detail = detail;
            this.expireAt = expireAt;
        }
    }
}
