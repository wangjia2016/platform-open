package com.platform.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @desc 参数无效项
 * 
 * @author wangjia
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParameterInvalidItem {

	/**
	 * 无效字段的名称
	 */
	private String fieldName;

	/**
	 * 错误信息
	 */
	private String message;

}
