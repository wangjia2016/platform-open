package com.platform.common.exception.handler;


import com.platform.common.result.DefaultErrorResult;
import com.platform.common.result.Result;
import com.platform.common.util.JsonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author wangjia
 * 拦截Controller方法默认返回参数，统一处理返回值/响应体
 */
@ControllerAdvice(basePackages = "com.platform")
public class ResponseHandlerAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 是否支持advice功能
     * treu=支持，false=不支持
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {

        return true;
    }

    /**
     *
     * 处理response的具体业务方法
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof String) {
           return JsonUtil.object2Json(Result.success(o));
        }

        //默认异常处理转换
        if (o instanceof DefaultErrorResult){
            DefaultErrorResult defaultErrorResult = (DefaultErrorResult) o;
            return Result.builder()
                    .code(defaultErrorResult.getCode())
                    .msg(defaultErrorResult.getMessage())
                    .data(defaultErrorResult.getError()+":"+defaultErrorResult.getException()+":"+defaultErrorResult.getErrors())
                    .build();
        }

        if (o instanceof Result) {
            Result result = (Result)o;
            return result;
        }
        return Result.success(o);
    }


}

