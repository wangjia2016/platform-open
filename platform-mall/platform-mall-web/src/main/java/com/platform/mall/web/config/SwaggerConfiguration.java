package com.platform.mall.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

/**
 * @Description
 * @ClassName SwaggerConfiguration
 * @Author wangjia
 * @date 2020.10.09 14:28
 */
@EnableOpenApi
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("商城服务C端api")
                .select().build();
    }

    private ApiInfo apiInfo(){
        // 作者信息
        Contact contact = new Contact("wj", "https://github.com/wangjia2016/platform-open", "vivianshine@126.com");

        return new ApiInfo(
                "api接口文档",
                "项目描述",
                "1.0",
                "https://github.com/wangjia2016/platform-open",
                contact,
                "Apache 2.0",
                "https://github.com/wangjia2016/platform-open",
                new ArrayList());
    }
}
