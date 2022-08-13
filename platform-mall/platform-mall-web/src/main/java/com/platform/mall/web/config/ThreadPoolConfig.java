package com.platform.mall.web.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.*;

/**
 * 线程池 -SpringBoot
 * */
//@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThreadPoolConfig {

    private final RedisTemplate redisTemplate;
    /**
     * 消费队列线程
     * @return
     */
    @Bean(value = "consumerQueueThreadPool")
    public ExecutorService buildConsumerQueueThreadPool(){
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("consumer-queue-thread-%d").build();
        final String key = "consumer-queue-thread";

        //corePoolSize 为线程池的基本大小。Runtime.getRuntime().availableProcessors()
        //maximumPoolSize 为线程池最大线程大小。
        //keepAliveTime 和 unit 则是线程空闲后的存活时间。
        //workQueue 用于存放任务的阻塞队列。
        //handler 当队列和最大线程池都满了之后的饱和策略。
        ExecutorService pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 100, 5L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(100),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
        redisTemplate.opsForValue().set(key,pool);
        return pool ;
    }

    @Bean(value = "forkJoinPool")
    public ForkJoinPool forkJoinPool(){

        return new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                true);

    }

}
