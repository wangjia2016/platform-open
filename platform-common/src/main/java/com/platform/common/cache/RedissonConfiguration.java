package com.platform.common.cache;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:Redisson配置
 * @author: 王佳  13:51 2017/12/23
 * @version: 1.0
 * @modify: MODIFIER'S NAME YYYY/MM/DD 修改内容简述
 * @Copyright: 版权信息
 */
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(RedissonSettings.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedissonConfiguration {

    private final RedissonSettings redissonSettings;

//    /**
//     * 哨兵模式自动装配
//     * @return
//     */
//    @Bean
//    @ConditionalOnProperty(name="redisson.master-name")
//    public RedissonClient redissonSentinel() {
//        Config config = new Config();
//        SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(redissonSettings.getSentinelAddresses())
//                .setMasterName(redissonSettings.getMasterName())
//                .setTimeout(redissonSettings.getTimeout())
//                .setMasterConnectionPoolSize(redissonSettings.getMasterConnectionPoolSize())
//                .setSlaveConnectionPoolSize(redissonSettings.getSlaveConnectionPoolSize());
//
//        if(StringUtils.isNotBlank(redissonSettings.getPassword())) {
//            serverConfig.setPassword(redissonSettings.getPassword());
//        }
//        return Redisson.create(config);
//    }

    /**
     * 单机模式自动装配
     */
    @Bean
    @ConditionalOnProperty(name="redisson.address")
    public RedissonClient redissonSingle() {

        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redissonSettings.getAddress())
                .setTimeout(redissonSettings.getTimeout())
                .setConnectionPoolSize(redissonSettings.getConnectionPoolSize())
                .setDatabase(redissonSettings.getDatabase())
                .setConnectionMinimumIdleSize(redissonSettings.getConnectionMinimumIdleSize());

        if(StringUtils.isNotBlank(redissonSettings.getPassword())) {
            serverConfig.setPassword(redissonSettings.getPassword());
        }

        return Redisson.create(config);
    }
}
