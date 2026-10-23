package com.platform.mall.service.flash.impl;

import com.platform.common.exception.BusinessException;
import com.platform.mall.dao.flash.entity.MallFlashSessions;
import com.platform.mall.dao.flash.entity.MallFlashSessionsGoods;
import com.platform.mall.dao.flash.mapper.MallFlashSessionsGoodsMapper;
import com.platform.mall.dao.flash.mapper.MallFlashSessionsMapper;
import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MallFlashSessionsServiceImplTest {

    private MallFlashSessionsMapper sessionsMapper;
    private MallFlashSessionsGoodsMapper sessionsGoodsMapper;
    private MallFlashSessionsServiceImpl service;

    @BeforeEach
    void setUp() {
        sessionsMapper = mock(MallFlashSessionsMapper.class);
        sessionsGoodsMapper = mock(MallFlashSessionsGoodsMapper.class);
        service = new MallFlashSessionsServiceImpl(sessionsMapper, sessionsGoodsMapper, mock(MallGoodsMapper.class));
    }

    @Test
    void rejectMissingSession() {
        when(sessionsMapper.selectById(1L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validateSessionForOrder(1L, 10L));
        assertEquals("秒杀场次不存在,下单失败", ex.getMessage());
    }

    @Test
    void rejectDisabledSession() {
        when(sessionsMapper.selectById(1L)).thenReturn(ongoingSession(0));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validateSessionForOrder(1L, 10L));
        assertEquals("秒杀场次未启用,下单失败", ex.getMessage());
    }

    @Test
    void rejectNotStartedSession() {
        long now = System.currentTimeMillis();
        MallFlashSessions session = MallFlashSessions.builder()
                .id(1L)
                .isEnable(1)
                .startTime(new Date(now + 60_000L))
                .endTime(new Date(now + 120_000L))
                .build();
        when(sessionsMapper.selectById(1L)).thenReturn(session);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validateSessionForOrder(1L, 10L));
        assertEquals("秒杀场次未开始,下单失败", ex.getMessage());
    }

    @Test
    void rejectEndedSession() {
        long now = System.currentTimeMillis();
        MallFlashSessions session = MallFlashSessions.builder()
                .id(1L)
                .isEnable(1)
                .startTime(new Date(now - 120_000L))
                .endTime(new Date(now - 60_000L))
                .build();
        when(sessionsMapper.selectById(1L)).thenReturn(session);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validateSessionForOrder(1L, 10L));
        assertEquals("秒杀场次已结束,下单失败", ex.getMessage());
    }

    @Test
    void rejectUnboundGoods() {
        when(sessionsMapper.selectById(1L)).thenReturn(ongoingSession(1));
        when(sessionsGoodsMapper.selectOne(any())).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validateSessionForOrder(1L, 10L));
        assertEquals("商品未绑定该秒杀场次,下单失败", ex.getMessage());
    }

    @Test
    void acceptOngoingEnabledBoundSession() {
        when(sessionsMapper.selectById(1L)).thenReturn(ongoingSession(1));
        when(sessionsGoodsMapper.selectOne(any())).thenReturn(
                MallFlashSessionsGoods.builder().sessionsId(1L).goodsId(10L).build());
        assertDoesNotThrow(() -> service.validateSessionForOrder(1L, 10L));
    }

    private static MallFlashSessions ongoingSession(int enable) {
        long now = System.currentTimeMillis();
        return MallFlashSessions.builder()
                .id(1L)
                .isEnable(enable)
                .startTime(new Date(now - 60_000L))
                .endTime(new Date(now + 60_000L))
                .build();
    }
}
