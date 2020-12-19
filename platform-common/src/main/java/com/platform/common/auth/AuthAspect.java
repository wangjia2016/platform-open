package com.platform.common.auth;

import com.platform.common.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * token 校验切面
 * @author Administrator
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthAspect {
    private final JwtOperator jwtOperator;

    /**
     * Authentication 认证
     * */
    @Around("@annotation(com.platform.common.auth.CheckAuthentication)")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {
        checkAuthentication();
        return point.proceed();
    }

    private void checkAuthentication() {
        //try {
            // 1. 从header里面获取token
            HttpServletRequest request = getHttpServletRequest();
            String token = request.getHeader("X-Token");
            if(StringUtils.isBlank(token)){
                throw new SecurityException("Token不能为空！");
            }
            // 2. 校验token是否合法&是否过期；如果不合法或已过期直接抛异常；如果合法放行
            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid) {
                throw new SecurityException("Token不合法！");
            }
            // 3. 如果校验成功，那么就将用户的信息设置到request的attribute里面
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
        final Object tenantId = claims.get("tenantId");

        if(tenantId!=null){
            request.setAttribute("tenantId", tenantId);
        }

//        } catch (Throwable throwable) {
//            throw new SecurityException("Token不合法");
//        }
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        return attributes.getRequest();
    }
}
