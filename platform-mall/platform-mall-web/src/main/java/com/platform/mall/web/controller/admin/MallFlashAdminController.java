package com.platform.mall.web.controller.admin;

import com.platform.common.result.Result;
import com.platform.mall.dao.flash.entity.MallFlashSessionsGoods;
import com.platform.mall.dao.flash.model.detail.MallFlashSessionsDetailDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsGoodsListDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsListDto;
import com.platform.mall.dao.flash.model.query.MallFlashSessionsGoodsBindQuery;
import com.platform.mall.dao.flash.model.query.MallFlashSessionsQuery;
import com.platform.mall.service.flash.MallFlashSessionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端：维护秒杀场次与场次-商品关联。
 */
@Api(tags = "管理后台-秒杀场次api")
@RestController
@RequestMapping("admin/flash")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallFlashAdminController {

    private final MallFlashSessionsService mallFlashSessionsService;

    @ApiOperation("场次列表")
    @GetMapping("/sessions/list")
    public Result<List<MallFlashSessionsListDto>> listSessions(MallFlashSessionsQuery query) {
        return Result.success(mallFlashSessionsService.listSessions(query == null ? new MallFlashSessionsQuery() : query));
    }

    @ApiOperation("保存/更新场次")
    @PostMapping("/sessions/save")
    public Result<MallFlashSessionsDetailDto> saveSession(@RequestBody MallFlashSessionsDetailDto detail) {
        return Result.success(mallFlashSessionsService.saveSession(detail));
    }

    @ApiOperation("场次商品列表")
    @GetMapping("/sessions/goods")
    public Result<List<MallFlashSessionsGoodsListDto>> sessionGoods(@RequestParam("sessionsId") Long sessionsId) {
        return Result.success(mallFlashSessionsService.listSessionGoods(sessionsId));
    }

    @ApiOperation("关联商品到场次")
    @PostMapping("/sessions/bindGoods")
    public Result<MallFlashSessionsGoods> bindGoods(@RequestBody MallFlashSessionsGoodsBindQuery bindQuery) {
        return Result.success(mallFlashSessionsService.bindGoods(bindQuery));
    }

    @ApiOperation("取消场次商品关联")
    @DeleteMapping("/sessions/unbindGoods")
    public Result unbindGoods(@RequestParam("sessionsId") Long sessionsId,
                              @RequestParam("goodsId") Long goodsId) {
        boolean ok = mallFlashSessionsService.unbindGoods(sessionsId, goodsId);
        return ok ? Result.success("取消关联成功") : Result.fail("取消关联失败或记录不存在");
    }
}
