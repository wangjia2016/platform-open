package com.platform.common.exception;

import com.platform.common.enums.BusinessExceptionEnum;
import com.platform.common.enums.ResultCode;
import com.platform.common.util.StringUtil;
import lombok.Data;

/**
 * @desc 业务异常类
 * 
 * @author wnagjia
 */
@Data
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 194906846739586856L;

	protected String code;

	protected String message;

	protected ResultCode resultCode;

	protected Object data;

	public BusinessException() {
		BusinessExceptionEnum exceptionEnum = BusinessExceptionEnum.getByEClass(this.getClass());
		if (exceptionEnum != null) {
			resultCode = exceptionEnum.getResultCode();
			code = exceptionEnum.getResultCode().code().toString();
			message = exceptionEnum.getResultCode().message();
		}

	}

	public BusinessException(String message) {
		this();
		this.message = message;
	}

	public BusinessException(ResultCode resultCode, Object data) {
		this(resultCode);
		this.data = data;
	}

	public BusinessException(ResultCode resultCode) {
		this.resultCode = resultCode;
		this.code = resultCode.code().toString();
		this.message = resultCode.message();
	}

	public BusinessException(String format, Object... objects) {
		this();
		this.message = StringUtil.formatIfArgs(format, "{}", objects);
	}
}
