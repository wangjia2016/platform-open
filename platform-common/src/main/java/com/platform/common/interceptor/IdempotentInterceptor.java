package com.platform.common.interceptor;

import com.platform.common.annotation.CheckIdempotent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Description
 * @ClassName IdempotentInterceptor
 * @Author wangjia
 * @date 2020.12.31 19:47
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IdempotentInterceptor implements HandlerInterceptor {

    private final RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Method method = handlerMethod.getMethod();
        // 有这个注解
        boolean hasAnnotation = method.isAnnotationPresent(CheckIdempotent.class);
        if (hasAnnotation && method.getAnnotation(CheckIdempotent.class).value()) {
            // 需要实现接口幂等性
            boolean result = checkToken(request);
            if (!result) {
                throw new SecurityException("不允许重复提交");
            }
        }
        return true;
    }

    private boolean checkToken(HttpServletRequest request) {
        String token = request.getHeader("I-token");
        if (StringUtils.isEmpty(token)) {
            // token不存在，说明重复调用或者 过期
            throw new SecurityException("不允许重复提交 || I-token 不存在");
        }
        // 返回是否删除成功
        return redisTemplate.delete(token);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
}
