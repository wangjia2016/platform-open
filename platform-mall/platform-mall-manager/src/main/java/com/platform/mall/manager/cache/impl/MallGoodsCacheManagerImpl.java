package com.platform.mall.manager.cache.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.common.enums.MallCacheKey;
import com.platform.mall.dao.goods.convert.MallGoodsConverter;
import com.platform.mall.dao.goods.entity.MallGoods;
import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.query.MallGoodsQuery;
import com.platform.mall.manager.cache.GoodsStockCache;
import com.platform.mall.manager.cache.MallGoodsCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 商品详情以明确类型的 JSON 写入 Redis，库存计数交给 {@link GoodsStockCache}。
 */
@Slf4j
@Service("mallGoodsCacheManager")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallGoodsCacheManagerImpl implements MallGoodsCacheManager {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final MallGoodsMapper mallGoodsMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final GoodsStockCache goodsStockCache;

    private final ConcurrentHashMap<Long, Object> loadLocks = new ConcurrentHashMap<>();

    @Override
    public Boolean mallGoodsCache() {
        List<MallGoods> mallGoodsList = mallGoodsMapper.selectList(
                new QueryWrapper<MallGoods>().lambda()
                        .eq(MallGoods::getOnUnSale, 0)
                        .eq(MallGoods::getAuditStatus, 2));
        if (CollectionUtils.isEmpty(mallGoodsList)) {
            log.error("商品为空，初始化商品资源失败");
            return false;
        }
        RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (MallGoods mallGoods : mallGoodsList) {
                writeGoods(connection, serializer, mallGoods);
            }
            return null;
        });
        log.info("[MallGoodsCache]初始化商品缓存完成,数量={}", mallGoodsList.size());
        return true;
    }

    @Override
    public MallGoodsDetailDto getMallGoods(Long id) {
        MallGoodsDetailDto cached = readGoods(id);
        if (cached != null) {
            seedStockIfAbsent(cached);
            return cached;
        }
        synchronized (loadLocks.computeIfAbsent(id, key -> new Object())) {
            cached = readGoods(id);
            if (cached != null) {
                seedStockIfAbsent(cached);
                return cached;
            }
            MallGoods mallGoods = mallGoodsMapper.selectById(id);
            if (mallGoods == null) {
                return null;
            }
            MallGoodsDetailDto detail = MallGoodsConverter.INSTANCE.toDTO(mallGoods);
            writeOne(detail);
            seedStockIfAbsent(detail);
            return detail;
        }
    }

    @Override
    public Boolean saveCache(MallGoods mallGoods) {
        MallGoodsDetailDto detail = MallGoodsConverter.INSTANCE.toDTO(mallGoods);
        writeOne(detail);
        if (detail.getStock() != null) {
            goodsStockCache.setStock(detail.getId(), detail.getStock());
        }
        return true;
    }

    @Override
    public Boolean deleteCache(MallGoodsQuery mallGoods) {
        stringRedisTemplate.delete(goodsKey(mallGoods.getId()));
        goodsStockCache.delete(mallGoods.getId());
        return true;
    }

    private void seedStockIfAbsent(MallGoodsDetailDto detail) {
        if (detail.getId() == null || detail.getStock() == null) {
            return;
        }
        if (goodsStockCache.getStock(detail.getId()) == null) {
            goodsStockCache.setStockIfAbsent(detail.getId(), detail.getStock());
        }
    }

    private void writeOne(MallGoodsDetailDto detail) {
        try {
            stringRedisTemplate.opsForValue().set(
                    goodsKey(detail.getId()),
                    OBJECT_MAPPER.writeValueAsString(detail),
                    GoodsStockCache.TTL_SECONDS,
                    TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("写入商品缓存失败,id={}", detail.getId(), ex);
        }
    }

    private void writeGoods(org.springframework.data.redis.connection.RedisConnection connection,
                            RedisSerializer<String> serializer,
                            MallGoods mallGoods) {
        MallGoodsDetailDto detail = MallGoodsConverter.INSTANCE.toDTO(mallGoods);
        try {
            byte[] goodsKey = serializer.serialize(goodsKey(mallGoods.getId()));
            byte[] goodsValue = serializer.serialize(OBJECT_MAPPER.writeValueAsString(detail));
            connection.setEx(goodsKey, GoodsStockCache.TTL_SECONDS, goodsValue);
        } catch (Exception ex) {
            log.error("预热商品详情失败,id={}", mallGoods.getId(), ex);
        }
        if (mallGoods.getStock() != null) {
            byte[] stockKey = serializer.serialize(goodsStockCache.stockKey(mallGoods.getId()));
            byte[] stockValue = serializer.serialize(String.valueOf(mallGoods.getStock()));
            connection.setEx(stockKey, GoodsStockCache.TTL_SECONDS, stockValue);
        }
    }

    private MallGoodsDetailDto readGoods(Long id) {
        String json = stringRedisTemplate.opsForValue().get(goodsKey(id));
        if (json == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, MallGoodsDetailDto.class);
        } catch (Exception ex) {
            log.warn("商品缓存无法按详情类型解析,回源,id={}", id);
            stringRedisTemplate.delete(goodsKey(id));
            return null;
        }
    }

    private String goodsKey(Long id) {
        return MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue() + id;
    }
}
