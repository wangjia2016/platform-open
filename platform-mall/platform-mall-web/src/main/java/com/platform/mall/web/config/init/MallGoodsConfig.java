package com.platform.mall.web.config.init;

import com.platform.mall.manager.cache.MallGoodsCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 启动时把上架且审核通过的商品详情、库存计数写入 Redis。
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallGoodsConfig implements InitializingBean {

    private final MallGoodsCacheManager mallGoodsCacheManager;

    @Override
    public void afterPropertiesSet() {
        Boolean ready = mallGoodsCacheManager.mallGoodsCache();
        log.info("[MallGoodsConfig]商品缓存初始化结果={}", ready);
    }
}
