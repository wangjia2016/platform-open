package com.platform.mall.service.flash;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.mall.dao.flash.entity.MallFlashSessions;
import com.platform.mall.dao.flash.entity.MallFlashSessionsGoods;
import com.platform.mall.dao.flash.model.detail.MallFlashSessionsDetailDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsGoodsListDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsListDto;
import com.platform.mall.dao.flash.model.query.MallFlashSessionsGoodsBindQuery;
import com.platform.mall.dao.flash.model.query.MallFlashSessionsQuery;

import java.util.List;

public interface MallFlashSessionsService extends IService<MallFlashSessions> {

    /**
     * 场次列表（含状态/倒计时）。门户调用时默认只返回启用场次。
     */
    List<MallFlashSessionsListDto> listSessions(MallFlashSessionsQuery query);

    MallFlashSessionsDetailDto getSessionDetail(Long id);

    MallFlashSessionsDetailDto saveSession(MallFlashSessionsDetailDto detail);

    List<MallFlashSessionsGoodsListDto> listSessionGoods(Long sessionsId);

    MallFlashSessionsGoods bindGoods(MallFlashSessionsGoodsBindQuery bindQuery);

    boolean unbindGoods(Long sessionsId, Long goodsId);
}
