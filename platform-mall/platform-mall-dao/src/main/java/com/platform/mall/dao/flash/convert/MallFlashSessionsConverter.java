package com.platform.mall.dao.flash.convert;

import com.platform.mall.dao.flash.entity.MallFlashSessions;
import com.platform.mall.dao.flash.model.detail.MallFlashSessionsDetailDto;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsListDto;
import com.platform.mall.dao.flash.model.query.MallFlashSessionsQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MallFlashSessionsConverter {

    MallFlashSessionsConverter INSTANCE = Mappers.getMapper(MallFlashSessionsConverter.class);

    MallFlashSessionsDetailDto toDTO(MallFlashSessions entity);

    List<MallFlashSessionsListDto> toListDTO(List<MallFlashSessions> list);

    MallFlashSessions fromQuery(MallFlashSessionsQuery query);

    MallFlashSessions fromDetail(MallFlashSessionsDetailDto dto);
}
