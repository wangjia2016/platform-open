package com.platform.mall.service.flash.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.BusinessException;
import com.platform.mall.dao.flash.convert.MallFlashSessionsConverter;
import com.platform.mall.dao.flash.entity.MallFlashSessions;
import com.platform.mall.dao.flash.entity.MallFlashSessionsGoods;
import com.platform.mall.dao.flash.mapper.MallFlashSessionsGoodsMapper;
import com.platform.mall.dao.flash.mapper.MallFlashSessionsMapper;
import com.platform.mall.dao.flash.model.detail.MallFlashSessionsDetailDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsGoodsListDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsListDto;
import com.platform.mall.dao.flash.model.query.MallFlashSessionsGoodsBindQuery;
import com.platform.mall.dao.flash.model.query.MallFlashSessionsQuery;
import com.platform.mall.dao.goods.entity.MallGoods;
import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import com.platform.mall.service.flash.FlashSessionStatus;
import com.platform.mall.service.flash.MallFlashSessionsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service("mallFlashSessionsService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallFlashSessionsServiceImpl extends ServiceImpl<MallFlashSessionsMapper, MallFlashSessions>
        implements MallFlashSessionsService {

    private final MallFlashSessionsMapper mallFlashSessionsMapper;
    private final MallFlashSessionsGoodsMapper mallFlashSessionsGoodsMapper;
    private final MallGoodsMapper mallGoodsMapper;

    @Override
    public List<MallFlashSessionsListDto> listSessions(MallFlashSessionsQuery query) {
        if (query == null) {
            query = new MallFlashSessionsQuery();
        }
        LambdaQueryWrapper<MallFlashSessions> wrapper = new QueryWrapper<MallFlashSessions>().lambda()
                .eq(query.getId() != null, MallFlashSessions::getId, query.getId())
                .eq(query.getFlashPromotionId() != null, MallFlashSessions::getFlashPromotionId, query.getFlashPromotionId())
                .eq(query.getIsEnable() != null, MallFlashSessions::getIsEnable, query.getIsEnable())
                .eq(StringUtils.isNotBlank(query.getTenantId()), MallFlashSessions::getTenantId, query.getTenantId())
                .like(StringUtils.isNotBlank(query.getName()), MallFlashSessions::getName, query.getName())
                .orderByAsc(MallFlashSessions::getListOrder)
                .orderByAsc(MallFlashSessions::getStartTime);
        List<MallFlashSessions> sessions = mallFlashSessionsMapper.selectList(wrapper);
        List<MallFlashSessionsListDto> list = MallFlashSessionsConverter.INSTANCE.toListDTO(sessions);
        Date now = new Date();
        for (MallFlashSessionsListDto dto : list) {
            dto.setStatus(FlashSessionStatus.resolve(dto.getStartTime(), dto.getEndTime(), now));
            dto.setCountdownSeconds(FlashSessionStatus.countdownSeconds(dto.getStartTime(), dto.getEndTime(), now));
        }
        return list;
    }

    @Override
    public MallFlashSessionsDetailDto getSessionDetail(Long id) {
        if (id == null) {
            throw new BusinessException("场次id不能为空");
        }
        MallFlashSessions session = mallFlashSessionsMapper.selectById(id);
        if (session == null) {
            return null;
        }
        return MallFlashSessionsConverter.INSTANCE.toDTO(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallFlashSessionsDetailDto saveSession(MallFlashSessionsDetailDto detail) {
        if (detail == null) {
            throw new BusinessException("场次参数为空");
        }
        if (StringUtils.isBlank(detail.getName())) {
            throw new BusinessException("场次名称不能为空");
        }
        if (detail.getStartTime() == null || detail.getEndTime() == null) {
            throw new BusinessException("场次起止时间不能为空");
        }
        if (!detail.getEndTime().after(detail.getStartTime())) {
            throw new BusinessException("场次结束时间必须晚于开始时间");
        }
        MallFlashSessions entity = MallFlashSessionsConverter.INSTANCE.fromDetail(detail);
        if (entity.getIsEnable() == null) {
            entity.setIsEnable(1);
        }
        if (entity.getListOrder() == null) {
            entity.setListOrder(0);
        }
        if (entity.getTenantId() == null) {
            entity.setTenantId("0");
        }
        if (entity.getId() == null) {
            mallFlashSessionsMapper.insert(entity);
        } else {
            mallFlashSessionsMapper.updateById(entity);
        }
        return MallFlashSessionsConverter.INSTANCE.toDTO(entity);
    }

    @Override
    public List<MallFlashSessionsGoodsListDto> listSessionGoods(Long sessionsId) {
        if (sessionsId == null) {
            throw new BusinessException("场次id不能为空");
        }
        List<MallFlashSessionsGoodsListDto> goods = mallFlashSessionsGoodsMapper.listGoodsBySessionsId(sessionsId);
        return goods == null ? Collections.emptyList() : goods;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallFlashSessionsGoods bindGoods(MallFlashSessionsGoodsBindQuery bindQuery) {
        if (bindQuery == null || bindQuery.getSessionsId() == null || bindQuery.getGoodsId() == null) {
            throw new BusinessException("场次id与商品id不能为空");
        }
        MallFlashSessions session = mallFlashSessionsMapper.selectById(bindQuery.getSessionsId());
        if (session == null) {
            throw new BusinessException("秒杀场次不存在");
        }
        MallGoods goods = mallGoodsMapper.selectById(bindQuery.getGoodsId());
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        MallFlashSessionsGoods existing = mallFlashSessionsGoodsMapper.selectOne(
                new QueryWrapper<MallFlashSessionsGoods>().lambda()
                        .eq(MallFlashSessionsGoods::getSessionsId, bindQuery.getSessionsId())
                        .eq(MallFlashSessionsGoods::getGoodsId, bindQuery.getGoodsId()));
        if (existing != null) {
            existing.setFlashPrice(bindQuery.getFlashPrice());
            existing.setFlashStock(bindQuery.getFlashStock());
            if (bindQuery.getListOrder() != null) {
                existing.setListOrder(bindQuery.getListOrder());
            }
            mallFlashSessionsGoodsMapper.updateById(existing);
            return existing;
        }
        MallFlashSessionsGoods relation = MallFlashSessionsGoods.builder()
                .sessionsId(bindQuery.getSessionsId())
                .goodsId(bindQuery.getGoodsId())
                .flashPrice(bindQuery.getFlashPrice())
                .flashStock(bindQuery.getFlashStock())
                .listOrder(bindQuery.getListOrder() == null ? 0 : bindQuery.getListOrder())
                .tenantId(StringUtils.isBlank(bindQuery.getTenantId()) ? "0" : bindQuery.getTenantId())
                .build();
        mallFlashSessionsGoodsMapper.insert(relation);
        return relation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindGoods(Long sessionsId, Long goodsId) {
        if (sessionsId == null || goodsId == null) {
            throw new BusinessException("场次id与商品id不能为空");
        }
        return mallFlashSessionsGoodsMapper.delete(new QueryWrapper<MallFlashSessionsGoods>().lambda()
                .eq(MallFlashSessionsGoods::getSessionsId, sessionsId)
                .eq(MallFlashSessionsGoods::getGoodsId, goodsId)) > 0;
    }
}
