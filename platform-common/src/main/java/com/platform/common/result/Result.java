package com.platform.common.result;

import com.platform.common.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangjia
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SuppressWarnings("unchecked")
public class Result<T> {
    /**
     * status状态值：代表本次请求response的状态结果。
     */
    private Integer code;
    /**
     * response描述：对本次状态码的描述。
     */
    private String msg;
    /**
     * data数据：本次返回的数据。
     */
    private T data;

    /**
     * 成功，创建ResResult：没data数据
     */
    public static Result success() {
        Result result = new Result();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    /**
     * 成功，创建ResResult：有data数据
     */
    public static Result success(Object data,String msg) {
        Result result = new Result();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    /**
     * 成功，创建ResResult：有data数据
     */
    public static Result success(Object data) {
        Result result = new Result();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 失败，指定status、desc
     */
    public static Result fail(Integer status, String desc) {
        Result result = new Result();
        result.setCode(status);
        result.setMsg(desc);
        return result;
    }

    /**
     * 失败，指定ResultCode枚举
     */
    public static Result fail(ResultCode resultCode) {
        Result result = new Result();
        result.setResultCode(resultCode);
        return result;
    }

    /**
     * 失败，指定ResultCode枚举
     */
    public static Result fail(ResultCode resultCode,String msg) {
        Result result = new Result();
        result.setResultCode(resultCode);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败，指定ResultCode枚举
     */
    public static Result fail(String msg) {
        Result result = new Result();
        result.setResultCode(ResultCode.SYSTEM_INNER_ERROR);
        result.setMsg(msg);
        return result;
    }

    /**
     * 把ResultCode枚举转换为ResResult
     */
    private void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }
}

