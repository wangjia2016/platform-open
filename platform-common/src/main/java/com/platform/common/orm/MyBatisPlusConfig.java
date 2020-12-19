package com.platform.common.orm;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.platform.common.util.SnowFlakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Lu Hao
 * @create: 2019-07-24 17:28
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public SnowFlakeIdGenerator getSnowflakeKeyGenerator(@Value("${keyGenerator.snowflake.workerId}")Integer workerId,
                                                         @Value("${keyGenerator.snowflake.datacenterId}")Integer datacenterId){
        return new SnowFlakeIdGenerator(workerId,datacenterId);
    }

}
