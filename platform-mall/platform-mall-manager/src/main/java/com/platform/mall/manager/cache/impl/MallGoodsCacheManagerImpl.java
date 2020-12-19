package com.platform.mall.manager.cache.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.enums.MallCacheKey;
import com.platform.common.util.JsonUtil;
import com.platform.mall.dao.goods.convert.MallGoodsConverter;
import com.platform.mall.dao.goods.entity.MallGoods;
import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.query.MallGoodsQuery;
import com.platform.mall.manager.cache.MallGoodsCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @ClassName MallGoodsCacheManagerImpl
 * @Author wangjia
 * @date 2020.12.08 21:52
 */
@Slf4j
@Service("mallGoodsCacheManager")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallGoodsCacheManagerImpl implements MallGoodsCacheManager {

    private final MallGoodsMapper mallGoodsMapper;

    private final RedisTemplate redisTemplate;

    @Override
    public Boolean mallGoodsCache(){
        //将上架了的，审核通过了的商品加入缓存
        List<MallGoods> mallGoodsList = mallGoodsMapper.selectList(
                new QueryWrapper<MallGoods>().lambda()
                        .eq(MallGoods::getOnUnSale,0)
                        .eq(MallGoods::getAuditStatus,2));
        if (CollectionUtils.isEmpty(mallGoodsList)) {
            //throw new BusinessException("商品为空，请检查");
            log.error("商品为空，初始化商品资源失败");
            return false;
        }
        mallGoodsList.stream().forEach((mallGoods -> {
            Long id = mallGoods.getId();
            Integer stock = mallGoods.getStock();
            //缓存24小时
            redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue()+id,mallGoods, 86400, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_SEMAPHORE_PREFIX.getTypeValue()+id,stock, 86400, TimeUnit.SECONDS);
        }));
        log.info("[MallGoodsConfig]初始化秒杀配置完成,商品信息=[{}]", JsonUtil.object2Json(mallGoodsList));
        return true;
    }

    @Override
    public MallGoodsDetailDto getMallGoods(Long id) {

        MallGoodsDetailDto mallGoods = (MallGoodsDetailDto)  redisTemplate.opsForValue().get(MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue()+id);

        if(mallGoods==null){
            MallGoods mallGoodsTmp = mallGoodsMapper.selectById(id);
            mallGoods = MallGoodsConverter.INSTANCE.toDTO(mallGoodsTmp);
            //缓存24小时
            redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue()+id,mallGoods, 7200, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_SEMAPHORE_PREFIX.getTypeValue()+id,mallGoods.getStock(), 7200, TimeUnit.SECONDS);
        }
        return mallGoods;
    }

    @Override
    public Boolean saveCache(MallGoods mallGoods) {
        redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue()+mallGoods.getId(),mallGoods, 7200, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_SEMAPHORE_PREFIX.getTypeValue()+mallGoods.getId(),mallGoods.getStock(), 7200, TimeUnit.SECONDS);

        return null;
    }

    @Override
    public Boolean deleteCache(MallGoodsQuery mallGoods) {
        redisTemplate.delete(MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue()+mallGoods.getId());
        redisTemplate.delete(MallCacheKey.NORMAL_GOODS_SEMAPHORE_PREFIX.getTypeValue()+mallGoods.getId());
        return null;
    }
}
