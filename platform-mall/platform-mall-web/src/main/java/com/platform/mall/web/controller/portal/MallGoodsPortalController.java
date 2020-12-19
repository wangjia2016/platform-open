package com.platform.mall.web.controller.portal;

import com.platform.common.result.Result;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.service.goods.MallGoodsService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 商品表-Controller
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-18 13:10:42
 */
@RestController
@RequestMapping("api/goods")
@Api(tags = "前端商品api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallGoodsPortalController  {

    private final MallGoodsService mallGoodsService;

    /**
     * @Description 查询商品详情
     * @Param id
     * @return Result
     * @Author wangjia
     * @Date 2020-10-02 17:02:42
     **/
    @ApiOperation("商品详情")
    @GetMapping("/queryMallGoods")
    public Result<MallGoodsDetailDto> queryMallGoods(@ApiParam("主键id")@RequestParam(value="id") Long id){
        MallGoodsDetailDto mallGoods = mallGoodsService.queryGoodsDetail(id);
        return Result.success(mallGoods);
    }


}


