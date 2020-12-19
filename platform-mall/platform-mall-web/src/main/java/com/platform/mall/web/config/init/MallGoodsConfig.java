package com.platform.mall.web.config.init;

import com.platform.common.enums.MallCacheKey;
import com.platform.common.util.JsonUtil;
import com.platform.mall.dao.goods.convert.MallGoodsConverter;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.list.MallGoodsListDto;
import com.platform.mall.dao.goods.model.query.MallGoodsQuery;
import com.platform.mall.service.goods.MallGoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description 普通商品、库存等信息预加载
 * @ClassName MallGoodsConfig
 * @Author wangjia
 * @date 2020.11.07 09:55
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallGoodsConfig implements InitializingBean {

    private final MallGoodsService mallGoodsService;

    private final RedisTemplate redisTemplate;

    /**
     * 这里也可以完成初始化
     * */
    @PostConstruct
    public void init() {

    }

    /**
     * 初始化bean时会执行该方法，调用优先级高于 init方法
     * 如果调用该方法时出错会直接抛出异常，不会再调用init方法
     * */
    @Override
    public void afterPropertiesSet()  {

        //将上架了的，审核通过了的商品加入内存
        List<MallGoodsListDto> mallGoodsList = mallGoodsService.listMallGoods(MallGoodsQuery.builder()
                .onUnSale(0)
                .auditStatus(2)
                .build());
        if (CollectionUtils.isEmpty(mallGoodsList)) {
            log.error("商品为空，初始化商品资源失败");
        }
        mallGoodsList.stream().forEach((mallGoods -> {
            Long id = mallGoods.getId();
            Integer stock = mallGoods.getStock();
            final MallGoodsDetailDto mallGoodsDetailDto = MallGoodsConverter.INSTANCE.listDtoToDetail(mallGoods);
            //缓存24小时
            redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue()+id,mallGoodsDetailDto, 7200, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_SEMAPHORE_PREFIX.getTypeValue()+id,stock, 7200, TimeUnit.SECONDS);
        }));
        log.info("[MallGoodsConfig]初始化秒杀配置完成,商品信息=[{}]", JsonUtil.object2Json(mallGoodsList));
    }
}
