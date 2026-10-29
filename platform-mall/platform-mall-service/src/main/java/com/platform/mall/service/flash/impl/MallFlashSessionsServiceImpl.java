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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service("mallFlashSessionsService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallFlashSessionsServiceImpl extends ServiceImpl<MallFlashSessionsMapper, MallFlashSessions>
        implements MallFlashSessionsService {

    private static final long ORDER_CACHE_TTL_NANOS = TimeUnit.SECONDS.toNanos(5);

    private final MallFlashSessionsMapper mallFlashSessionsMapper;
    private final MallFlashSessionsGoodsMapper mallFlashSessionsGoodsMapper;
    private final MallGoodsMapper mallGoodsMapper;

    private final ConcurrentHashMap<Long, CachedSession> sessionCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Long>> bindingCache = new ConcurrentHashMap<>();

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
        sessionCache.remove(entity.getId());
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
        forgetBinding(bindQuery.getSessionsId(), bindQuery.getGoodsId());
        return relation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindGoods(Long sessionsId, Long goodsId) {
        if (sessionsId == null || goodsId == null) {
            throw new BusinessException("场次id与商品id不能为空");
        }
        boolean removed = mallFlashSessionsGoodsMapper.delete(new QueryWrapper<MallFlashSessionsGoods>().lambda()
                .eq(MallFlashSessionsGoods::getSessionsId, sessionsId)
                .eq(MallFlashSessionsGoods::getGoodsId, goodsId)) > 0;
        forgetBinding(sessionsId, goodsId);
        return removed;
    }

    @Override
    public void validateSessionForOrder(Long sessionsId, Long goodsId) {
        if (sessionsId == null) {
            throw new BusinessException("下单必要参数[sessionsId]不能为空,下单失败");
        }
        if (goodsId == null) {
            throw new BusinessException("秒杀下单必要参数[goodsId]为空,下单失败");
        }
        MallFlashSessions session = loadSession(sessionsId);
        if (session == null) {
            throw new BusinessException("秒杀场次不存在,下单失败");
        }
        if (session.getIsEnable() == null || session.getIsEnable() != 1) {
            throw new BusinessException("秒杀场次未启用,下单失败");
        }
        int status = FlashSessionStatus.resolve(session.getStartTime(), session.getEndTime(), new Date());
        if (status == FlashSessionStatus.NOT_STARTED) {
            throw new BusinessException("秒杀场次未开始,下单失败");
        }
        if (status == FlashSessionStatus.ENDED) {
            throw new BusinessException("秒杀场次已结束,下单失败");
        }
        if (!isBound(sessionsId, goodsId)) {
            throw new BusinessException("商品未绑定该秒杀场次,下单失败");
        }
    }

    private MallFlashSessions loadSession(Long sessionsId) {
        long now = System.nanoTime();
        CachedSession cached = sessionCache.get(sessionsId);
        if (cached != null && cached.expireAt > now) {
            return cached.session;
        }
        MallFlashSessions session = mallFlashSessionsMapper.selectById(sessionsId);
        if (session != null) {
            sessionCache.put(sessionsId, new CachedSession(session, now + ORDER_CACHE_TTL_NANOS));
        }
        return session;
    }

    private boolean isBound(Long sessionsId, Long goodsId) {
        ConcurrentHashMap<Long, Long> goods = bindingCache.computeIfAbsent(sessionsId, key -> new ConcurrentHashMap<>());
        long now = System.nanoTime();
        Long expireAt = goods.get(goodsId);
        if (expireAt != null && expireAt > now) {
            return true;
        }
        MallFlashSessionsGoods relation = mallFlashSessionsGoodsMapper.selectOne(
                new QueryWrapper<MallFlashSessionsGoods>().lambda()
                        .eq(MallFlashSessionsGoods::getSessionsId, sessionsId)
                        .eq(MallFlashSessionsGoods::getGoodsId, goodsId));
        if (relation == null) {
            goods.remove(goodsId);
            return false;
        }
        goods.put(goodsId, now + ORDER_CACHE_TTL_NANOS);
        return true;
    }

    private void forgetBinding(Long sessionsId, Long goodsId) {
        if (sessionsId == null) {
            return;
        }
        if (goodsId == null) {
            bindingCache.remove(sessionsId);
            return;
        }
        ConcurrentHashMap<Long, Long> goods = bindingCache.get(sessionsId);
        if (goods != null) {
            goods.remove(goodsId);
        }
    }

    private static final class CachedSession {
        private final MallFlashSessions session;
        private final long expireAt;

        private CachedSession(MallFlashSessions session, long expireAt) {
            this.session = session;
            this.expireAt = expireAt;
        }
    }

}
