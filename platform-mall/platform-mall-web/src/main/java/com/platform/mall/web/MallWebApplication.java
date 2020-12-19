package com.platform.mall.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Description
 * @ClassName MallWebApplication
 * @Author wangjia
 * @date 2020.10.02 10:48
 */
@SpringBootApplication
@MapperScan("com.platform.mall.dao.*.mapper")
@ComponentScan({"com.platform.mall","com.platform.common.*"})
@EnableFeignClients("com.platform.mall.service.feignClient")
@EnableAsync
public class MallWebApplication {

    public static void main(String[] args) {
        System.setProperty("nacos.logging.default.config.enabled", "false");
        SpringApplication.run(MallWebApplication.class, args);
    }

}
