package com.platform.mall.web.config.init;

import com.platform.mall.dao.threadpool.model.list.ThreadPoolConfigListDto;
import com.platform.mall.dao.threadpool.model.query.ThreadPoolConfigQuery;
import com.platform.mall.service.threadpool.ThreadPoolConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 用库里的核心/最大线程数调整已经注册的 consumerQueueThreadPool，不再另起一个执行器。
 */
@Slf4j
@Configuration("orderThreadPoolTuner")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThreadPoolConfig implements InitializingBean {

    private final ThreadPoolConfigService threadPoolConfigService;

    @Qualifier("consumerQueueThreadPool")
    private final ThreadPoolExecutor consumerQueueThreadPool;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void afterPropertiesSet() {
        List<ThreadPoolConfigListDto> configs;
        try {
            configs = threadPoolConfigService.listThreadPoolConfig(ThreadPoolConfigQuery.builder().build());
        } catch (Exception ex) {
            log.error("读取线程池配置失败,继续使用默认 consumerQueueThreadPool", ex);
            return;
        }
        if (CollectionUtils.isEmpty(configs)) {
            log.info("线程池配置为空,使用默认 consumerQueueThreadPool");
            return;
        }
        for (ThreadPoolConfigListDto config : configs) {
            if (config.getServiceName() != null && config.getServiceName().equals(applicationName)) {
                apply(config);
            }
        }
    }

    private void apply(ThreadPoolConfigListDto config) {
        Integer core = config.getCorePoolSize();
        Integer maximum = config.getMaximumPoolSize();
        if (core == null || maximum == null || core <= 0 || maximum < core) {
            log.error("忽略非法线程池配置 name={} core={} max={}", config.getThreadPoolName(), core, maximum);
            return;
        }
        consumerQueueThreadPool.setMaximumPoolSize(maximum);
        consumerQueueThreadPool.setCorePoolSize(core);
        log.info("已按配置调整 consumerQueueThreadPool, name={} core={} max={}",
                config.getThreadPoolName(), core, maximum);
    }
}
