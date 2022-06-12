package com.platform.mall.dao.threadpool.convert;

import com.platform.mall.dao.threadpool.entity.ThreadPoolConfig;
import com.platform.mall.dao.threadpool.model.detail.ThreadPoolConfigDetailDto;
import com.platform.mall.dao.threadpool.model.list.ThreadPoolConfigListDto;
import com.platform.mall.dao.threadpool.model.query.ThreadPoolConfigQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
* @Description ThreadPoolConfig转换器
* @ClassName ThreadPoolConfigConverter
* @Author wangjia
* @date 2022-06-11 22:10:55
*/
@Mapper
public interface ThreadPoolConfigConverter {

    ThreadPoolConfigConverter INSTANCE = Mappers.getMapper(ThreadPoolConfigConverter.class);

    ThreadPoolConfigDetailDto toDTO(ThreadPoolConfig entity);

    List<ThreadPoolConfigListDto> toListDTO(List<ThreadPoolConfig> lst);

    ThreadPoolConfig fromDTO(ThreadPoolConfigDetailDto dto);

    ThreadPoolConfig fromQuery(ThreadPoolConfigQuery query);

}