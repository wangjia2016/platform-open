package com.platform.mall.manager.cache;

import com.platform.common.enums.MallCacheKey;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 商品可售库存只走这一把计数键，值是十进制字符串，供 Lua 原子扣减。
 */
@Service
public class GoodsStockCache {

    /**
     * 返回剩余库存；-1 不足，-2 键不存在或不是整数，-3 扣减数量非法。
     * 与 {@link #apply(Integer, int)} 的返回约定保持一致。
     */
    static final String DEDUCT_LUA =
            "local num = tonumber(ARGV[1]) "
                    + "if num == nil or num <= 0 then return -3 end "
                    + "local stock = tonumber(redis.call('GET', KEYS[1])) "
                    + "if stock == nil then return -2 end "
                    + "if stock < num then return -1 end "
                    + "return redis.call('DECRBY', KEYS[1], num)";

    public static final long TTL_SECONDS = 7200L;

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> deductScript;

    public GoodsStockCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(DEDUCT_LUA);
        script.setResultType(Long.class);
        this.deductScript = script;
    }

    public static long apply(Integer current, int num) {
        if (num <= 0) {
            return -3L;
        }
        if (current == null) {
            return -2L;
        }
        if (current < num) {
            return -1L;
        }
        return (long) current - num;
    }

    public String stockKey(Long goodsId) {
        return MallCacheKey.NORMAL_GOODS_SEMAPHORE_PREFIX.getTypeValue() + goodsId;
    }

    public long tryDeduct(Long goodsId, int num) {
        Long result = stringRedisTemplate.execute(
                deductScript,
                Collections.singletonList(stockKey(goodsId)),
                String.valueOf(num));
        return result == null ? -2L : result;
    }

    public void restore(Long goodsId, int num) {
        if (goodsId == null || num <= 0) {
            return;
        }
        stringRedisTemplate.opsForValue().increment(stockKey(goodsId), num);
    }

    public void setStock(Long goodsId, int stock) {
        stringRedisTemplate.opsForValue().set(stockKey(goodsId), String.valueOf(stock), TTL_SECONDS, TimeUnit.SECONDS);
    }

    public void setStockIfAbsent(Long goodsId, int stock) {
        stringRedisTemplate.opsForValue().setIfAbsent(stockKey(goodsId), String.valueOf(stock), TTL_SECONDS, TimeUnit.SECONDS);
    }

    public Integer getStock(Long goodsId) {
        String value = stringRedisTemplate.opsForValue().get(stockKey(goodsId));
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            stringRedisTemplate.delete(stockKey(goodsId));
            return null;
        }
    }

    public void delete(Long goodsId) {
        stringRedisTemplate.delete(stockKey(goodsId));
    }
}
