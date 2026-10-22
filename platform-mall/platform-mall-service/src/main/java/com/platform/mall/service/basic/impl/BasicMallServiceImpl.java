package com.platform.mall.service.basic.impl;

import com.platform.common.enums.ThreadPoolNameEnum;
import com.platform.common.exception.BusinessException;
import com.platform.mall.service.basic.BasicMallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 调整进程内的下单线程池。不再把线程池对象放进 Redis。
 */
@Service("basicMallService")
@Slf4j
public class BasicMallServiceImpl implements BasicMallService {

    private final ThreadPoolExecutor consumerQueueThreadPool;

    public BasicMallServiceImpl(@Qualifier("consumerQueueThreadPool") ThreadPoolExecutor consumerQueueThreadPool) {
        this.consumerQueueThreadPool = consumerQueueThreadPool;
    }

    @Override
    public Boolean updateThreadPoolConfig(Integer corePoolSize, Integer maximumPoolSize, ThreadPoolNameEnum poolName) {
        if (consumerQueueThreadPool == null) {
            throw new BusinessException("线程池为空");
        }
        try {
            if (maximumPoolSize != null) {
                consumerQueueThreadPool.setMaximumPoolSize(maximumPoolSize);
            }
            if (corePoolSize != null) {
                consumerQueueThreadPool.setCorePoolSize(corePoolSize);
            }
            log.info("核心线程大小:{},最大线程大小:{}",
                    consumerQueueThreadPool.getCorePoolSize(), consumerQueueThreadPool.getMaximumPoolSize());
        } catch (Exception ex) {
            log.error("线程池更新异常", ex);
            throw new BusinessException("线程池更新异常");
        }
        return true;
    }
}
