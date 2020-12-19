package com.platform.common.enums;

import com.platform.common.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.remoting.RemoteAccessException;

/**
 * @desc 异常、HTTP状态码、默认自定义返回码 映射类
 *
 * @author wangjia
 */
public enum BusinessExceptionEnum {

	/**
	 * 远程访问时错误
	 */
	/**
	 * 无访问权限
	 */
	FORBIDDEN(PermissionForbiddenException.class, HttpStatus.FORBIDDEN, ResultCode.PERMISSION_NO_ACCESS);

	private Class<? extends BusinessException> eClass;

	private HttpStatus httpStatus;

	private ResultCode resultCode;

	BusinessExceptionEnum(Class<? extends BusinessException> eClass, HttpStatus httpStatus, ResultCode resultCode) {
		this.eClass = eClass;
		this.httpStatus = httpStatus;
		this.resultCode = resultCode;
	}

	public Class<? extends BusinessException> getEClass() {
		return eClass;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public static BusinessExceptionEnum getByEClass(Class<? extends BusinessException> eClass) {
		if (eClass == null) {
			return null;
		}

		for (BusinessExceptionEnum exceptionEnum : BusinessExceptionEnum.values()) {
			if (eClass.equals(exceptionEnum.eClass)) {
				return exceptionEnum;
			}
		}

		return null;
	}
}
