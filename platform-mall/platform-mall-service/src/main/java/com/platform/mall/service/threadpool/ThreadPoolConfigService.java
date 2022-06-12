package com.platform.mall.service.threadpool;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.mall.dao.threadpool.entity.ThreadPoolConfig;
import com.platform.mall.dao.threadpool.model.detail.ThreadPoolConfigDetailDto;
import com.platform.mall.dao.threadpool.model.list.ThreadPoolConfigListDto;
import com.platform.mall.dao.threadpool.model.query.ThreadPoolConfigQuery;

import java.util.List;

/**
 * ThreadPoolConfig业务处理
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2022-06-11 22:10:55
 */
public interface ThreadPoolConfigService extends IService<ThreadPoolConfig> {

    /**
     * @Description 分页查询ThreadPoolConfig列表
     * @Param ThreadPoolConfigDto
     * @return
     * @Author wangjia
     * @Date 2022-06-11 22:10:55
     **/
    IPage listThreadPoolConfigPage(ThreadPoolConfigQuery threadPoolConfigQuery);

    /**
     * @Description 查询ThreadPoolConfig列表不分页
     * @Param ThreadPoolConfigDto
     * @return
     * @Author wangjia
     * @Date 2022-06-11 22:10:55
     **/
    List<ThreadPoolConfigListDto> listThreadPoolConfig(ThreadPoolConfigQuery threadPoolConfigQuery);

    /**
     * @Description 根据ThreadPoolConfigDto查询一条ThreadPoolConfig记录
     * @Param ThreadPoolConfigDto
     * @return
     * @Author wangjia
     * @Date 2022-06-11 22:10:55
     **/
    ThreadPoolConfigDetailDto getOne(ThreadPoolConfigQuery threadPoolConfigQuery);

    /**
     * @Description 初始化线程池
     * @return Boolean
     * @Author wangjia
     * @Date 2022-06-12 17:10:55
     **/
    Boolean initThreadPool();
}

