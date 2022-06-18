package com.platform.mall.web.config.init;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.platform.common.enums.ThreadPoolNameEnum;
import com.platform.common.result.Result;
import com.platform.common.util.RequestContextUtil;
import com.platform.mall.dao.threadpool.model.list.ThreadPoolConfigListDto;
import com.platform.mall.dao.threadpool.model.query.ThreadPoolConfigQuery;
import com.platform.mall.service.threadpool.ThreadPoolConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Description 线程池初始化
 * @ClassName ThreadPoolConfig
 * @Author wangjia
 * @date 2022.06.18 18:53
 */
@Slf4j
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThreadPoolConfig implements InitializingBean {

    private final RequestContextUtil requestContextUtil;

    private final ThreadPoolConfigService threadPoolConfigService;

    protected static Map<ThreadPoolNameEnum, ThreadPoolExecutor> threadPoolMap = new ConcurrentHashMap<>();

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 这里也可以完成初始化
     * */
    @PostConstruct
    public void init() {

    }

    /**
     * 初始化bean时会执行该方法，调用优先级高于 init方法
     * 如果调用该方法时出错会直接抛出异常，不会再调用init方法
     * */
    @Override
    public void afterPropertiesSet()  {

        final List<ThreadPoolConfigListDto> threadPoolConfigListDtoResult = threadPoolConfigService.listThreadPoolConfig(
                ThreadPoolConfigQuery.builder().build()
        );
        if (CollectionUtils.isEmpty(threadPoolConfigListDtoResult)) {
            // 为空 初始化默认参数配置线程池
            log.error("线程池配置为空,初始化失败");
        }
        threadPoolConfigListDtoResult.stream().forEach(threadPoolConfigListDto -> {
            // 查找当前业务系统匹配的线程池
            if (threadPoolConfigListDto.getServiceName().equals(applicationName)) {
                final String nameFormat = applicationName + "-" + threadPoolConfigListDto.getThreadPoolName();
                ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                        .setNameFormat(nameFormat).build();
                //corePoolSize 为线程池的基本大小。Runtime.getRuntime().availableProcessors()
                //maximumPoolSize 为线程池最大线程大小。
                //keepAliveTime 和 unit 则是线程空闲后的存活时间。
                //workQueue 用于存放任务的阻塞队列。
                //handler 当队列和最大线程池都满了之后的饱和策略。
                ThreadPoolExecutor pool = new ThreadPoolExecutor(threadPoolConfigListDto.getCorePoolSize(), threadPoolConfigListDto.getMaximumPoolSize(), 5L, TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<Runnable>(threadPoolConfigListDto.getQueueSize()),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());

                threadPoolMap.put(ThreadPoolNameEnum.ORDER_POOL, pool);
                final ThreadPoolExecutor threadPoolExecutor = threadPoolMap.get(ThreadPoolNameEnum.ORDER_POOL);
                log.info("获取到的线程池:核心线程数:{},最大线程数:{}",threadPoolExecutor.getCorePoolSize(),threadPoolExecutor.getMaximumPoolSize());
            }
        });
        if (ObjectUtils.isEmpty(threadPoolMap)) {
            log.error("threadPoolMap 为空");
            return;
        }
        threadPoolMap.forEach((threadPoolNameEnum, threadPoolExecutor) -> {
            // 注册到Spring中
            requestContextUtil.registerSingletonBean(threadPoolNameEnum.getTypeValue(),threadPoolExecutor);
        });
        log.info("线程池初始化配置完成");
    }
}
