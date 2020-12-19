package com.platform.common.qiniu;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @ClassName QiniuProperties
 * @Author wangjia
 * @date 2020.10.11 18:44
 */
@Component("qiniuProperties")
@ConfigurationProperties(prefix="qiniu")
@Configuration
@Data
public class QiniuProperties {

    private String accessKey;

    private String secretKey;

    /**
     * 空间名
     * */
    private String bucket;

    /***
     * CDN加速域名
     */
    private String cdnUrl;


}
