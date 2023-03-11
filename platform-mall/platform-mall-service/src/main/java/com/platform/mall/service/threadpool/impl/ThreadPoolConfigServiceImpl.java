package com.platform.mall.service.threadpool.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.result.CommonParam;
import com.platform.mall.dao.threadpool.convert.ThreadPoolConfigConverter;
import com.platform.mall.dao.threadpool.entity.ThreadPoolConfig;
import com.platform.mall.dao.threadpool.mapper.ThreadPoolConfigMapper;
import com.platform.mall.dao.threadpool.model.detail.ThreadPoolConfigDetailDto;
import com.platform.mall.dao.threadpool.model.list.ThreadPoolConfigListDto;
import com.platform.mall.dao.threadpool.model.query.ThreadPoolConfigQuery;
import com.platform.mall.service.threadpool.ThreadPoolConfigService;
import org.springframework.stereotype.Service;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service("threadPoolConfigService")
public class ThreadPoolConfigServiceImpl extends ServiceImpl<ThreadPoolConfigMapper, ThreadPoolConfig> implements ThreadPoolConfigService {

    @Override
    public IPage listThreadPoolConfigPage(ThreadPoolConfigQuery threadPoolConfigQuery) {
        ThreadPoolConfig threadPoolConfig = ThreadPoolConfigConverter.INSTANCE.fromQuery(threadPoolConfigQuery);

        IPage<ThreadPoolConfig> page = this.page(
                new Page<>(CommonParam.getOffSet(), CommonParam.getPageSize()),
                new QueryWrapper<ThreadPoolConfig>(threadPoolConfig)
        );

        //dto转换
        List<ThreadPoolConfigListDto> threadPoolConfigs = ThreadPoolConfigConverter.INSTANCE.toListDTO(page.getRecords());
        IPage<ThreadPoolConfigListDto> threadPoolConfigDtoIPage = new Page(page.getCurrent(),page.getSize(),page.getTotal());
        threadPoolConfigDtoIPage.setRecords(threadPoolConfigs);
        return threadPoolConfigDtoIPage;
    }

    @Override
    public List<ThreadPoolConfigListDto> listThreadPoolConfig(ThreadPoolConfigQuery threadPoolConfigQuery) {
        List<ThreadPoolConfig> threadPoolConfigList = this.baseMapper.selectList(new QueryWrapper<ThreadPoolConfig>(
                ThreadPoolConfigConverter.INSTANCE.fromQuery(threadPoolConfigQuery)));

        List<ThreadPoolConfigListDto> ThreadPoolConfigListDtoList = ThreadPoolConfigConverter.INSTANCE.toListDTO(threadPoolConfigList);
        return ThreadPoolConfigListDtoList;
    }

    @Override
    public ThreadPoolConfigDetailDto getOne(ThreadPoolConfigQuery threadPoolConfigQuery) {
        ThreadPoolConfig threadPoolConfig = this.getOne(new QueryWrapper<>(ThreadPoolConfigConverter.INSTANCE.fromQuery(threadPoolConfigQuery)));

        return ThreadPoolConfigConverter.INSTANCE.toDTO(threadPoolConfig);
    }

    @Override
    public Boolean initThreadPool() {
        return null;
    }
}