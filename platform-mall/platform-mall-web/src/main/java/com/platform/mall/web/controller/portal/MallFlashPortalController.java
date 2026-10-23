package com.platform.mall.web.controller.portal;

import com.platform.common.result.Result;
import com.platform.mall.dao.flash.model.detail.MallFlashSessionsDetailDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsGoodsListDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsListDto;
import com.platform.mall.dao.flash.model.query.MallFlashSessionsQuery;
import com.platform.mall.service.flash.MallFlashSessionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 门户秒杀场次：列表 + 场次商品。无需登录，便于倒计时页静态化前拉数据。
 */
@Api(tags = "前端秒杀场次api")
@RestController
@RequestMapping("api/flash")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallFlashPortalController {

    private final MallFlashSessionsService mallFlashSessionsService;

    @ApiOperation("秒杀场次列表")
    @GetMapping("/sessions/list")
    public Result<List<MallFlashSessionsListDto>> listSessions(
            @RequestParam(value = "flashPromotionId", required = false) Long flashPromotionId) {
        MallFlashSessionsQuery query = MallFlashSessionsQuery.builder()
                .flashPromotionId(flashPromotionId)
                .isEnable(1)
                .build();
        return Result.success(mallFlashSessionsService.listSessions(query));
    }

    @ApiOperation("秒杀场次详情")
    @GetMapping("/sessions/detail")
    public Result<MallFlashSessionsDetailDto> sessionDetail(@RequestParam("sessionsId") Long sessionsId) {
        MallFlashSessionsDetailDto detail = mallFlashSessionsService.getSessionDetail(sessionsId);
        if (detail == null) {
            return Result.fail("场次不存在");
        }
        return Result.success(detail);
    }

    @ApiOperation("场次关联商品列表")
    @GetMapping("/sessions/goods")
    public Result<List<MallFlashSessionsGoodsListDto>> sessionGoods(@RequestParam("sessionsId") Long sessionsId) {
        return Result.success(mallFlashSessionsService.listSessionGoods(sessionsId));
    }
}
