package com.platform.common.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: 王佳  11:33 2017/11/14
 * @version: 1.0
 * @modify: MODIFIER'S NAME YYYY/MM/DD 修改内容简述
 * @Copyright: 版权信息
 */
@Component("redisConfigProperties")
@ConfigurationProperties(prefix="redis")
@Configuration
@Data
public class RedisConfigProperties {

    private String host;
    private Integer port;
    private Integer timeout;
    private String password;
    private Integer database;

    private Map<String, String> pool = new HashMap<>();

}
