package com.platform.mall.dao.goods.mapper;

import com.platform.mall.dao.goods.entity.MallGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:02:42
 */
@Mapper
public interface MallGoodsMapper extends BaseMapper<MallGoods> {

    /**
     * @Description 级联查询商品富文本信息
     * @Param MallGoodsDto
     * @return
     * @Author wangjia
     * @Date 2020-10-15 17:02:42
     **/
    MallGoodsDetailDto queryGoodsDetail(Long id);

    /**
     * @Description: 下单成功，更新商品库存
     * @param @param orderNo
     * @param @return
     * @return Integer
     * @throws
     * @Author: 王佳
     * @date 2020年10月6日
     */
     Integer updateGoodsStock(Long id,Integer number);

    /**
     * @Description: 取消订单-返回库存
     * @param @param Long id
     * @param @return
     * @return Integer
     * @throws
     * @Author: 王佳
     * @date 2020年11月8日
     */
     Integer updateGoodStockBack(Long id);
}

