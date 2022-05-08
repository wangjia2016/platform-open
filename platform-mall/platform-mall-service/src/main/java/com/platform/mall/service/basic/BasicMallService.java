package com.platform.mall.service.basic;

import com.platform.common.enums.ThreadPoolNameEnum;

public interface BasicMallService {

    /**
     * 调整线程池大小
     * @param corePoolSize  核心线程大小
     * @param maximumPoolSize 最大线程大小
     * @param poolName 线程池名称
     * @return
     * @author wangjia
     * @date 2022.05.08 11:52
     **/
    Boolean updateThreadPoolConfig(Integer corePoolSize, Integer maximumPoolSize, ThreadPoolNameEnum poolName);
}
