package com.platform.mall.web.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 下单路径上的有界线程池。队列满时由调用线程执行，避免直接丢任务。
 */
@Configuration("consumerQueueThreadPoolConfig")
public class ThreadPoolConfig {

    @Bean(name = "consumerQueueThreadPool")
    public ThreadPoolExecutor consumerQueueThreadPool() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("consumer-queue-thread-%d")
                .build();
        int processors = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(
                processors,
                100,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                namedThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
