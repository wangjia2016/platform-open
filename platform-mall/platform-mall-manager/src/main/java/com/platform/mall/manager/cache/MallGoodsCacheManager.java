package com.platform.mall.manager.cache;

import com.platform.mall.dao.goods.entity.MallGoods;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.query.MallGoodsQuery;

/**
 * @description: 商品缓存manager
 * @author: 王佳  15:30 2020/11/24
 * @version: 1.0
 * @modify: MODIFIER'S NAME YYYY/MM/DD 修改内容简述
 * @Copyright: 版权信息
 */
public interface MallGoodsCacheManager {

    /**
     * 全量刷新商品缓存数据
     * */
    Boolean mallGoodsCache();

    /**
     * 从缓存获取mallGoods
     * @Param Long id
     * @Return MallGoodsDetailDto
     * */
    MallGoodsDetailDto getMallGoods(Long id);

    /**
     * 插入缓存
     * */
    Boolean saveCache(MallGoods mallGoods);

    /**
     * 删除缓存
     * */
    Boolean deleteCache(MallGoodsQuery mallGoods);
}
