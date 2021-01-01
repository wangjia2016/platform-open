package com.platform.mall.web.config;

import com.platform.common.interceptor.IdempotentInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description web配置 包括拦截器等
 * @ClassName WebConfig
 * @Author wangjia
 * @date 2020.12.31 19:50
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebConfig implements WebMvcConfigurer {

    public final IdempotentInterceptor idempotentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(idempotentInterceptor).addPathPatterns("/api/goods/*");
        registry.addInterceptor(idempotentInterceptor).addPathPatterns("/api/order/*");
    }

}
