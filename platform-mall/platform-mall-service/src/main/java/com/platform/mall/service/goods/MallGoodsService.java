package com.platform.mall.service.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.platform.mall.dao.goods.entity.MallGoods;
import com.platform.mall.dao.goods.model.list.MallGoodsListDto;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.query.MallGoodsQuery;

import java.util.List;

/**
 * MallGoods业务处理
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:02:42
 */
public interface MallGoodsService extends IService<MallGoods> {

    /**
     * @Description 查询MallGoods列表不分页
     * @Param MallGoodsDto
     * @return
     * @Author wangjia
     * @Date 2020-10-02 17:02:42
     **/
    List<MallGoodsListDto> listMallGoods(MallGoodsQuery mallGoodsQuery);

    /**
     * @Description 根据MallGoodsDto查询一条MallGoods记录
     * @Param MallGoodsDto
     * @return
     * @Author wangjia
     * @Date 2020-10-02 17:02:42
     **/
    MallGoodsDetailDto getOne(MallGoodsQuery mallGoodsQuery);

    /**
     * @Description 级联查询商品富文本信息
     * @Param MallGoodsDto
     * @return
     * @Author wangjia
     * @Date 2020-10-15 17:02:42
     **/
    MallGoodsDetailDto queryGoodsDetail(Long id);
}

