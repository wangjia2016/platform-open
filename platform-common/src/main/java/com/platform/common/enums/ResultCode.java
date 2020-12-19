package com.platform.common.enums;

/**
 * @desc API 统一返回状态码
 * 
 * @author wangjia
 */
public enum ResultCode {

	/* 成功状态码 */
	SUCCESS(1, "成功"),

	/* 参数错误：100001-199999 */
	PARAM_IS_INVALID(100001, "参数无效"),
	PARAM_IS_BLANK(100002, "参数为空"),
	PARAM_TYPE_BIND_ERROR(100003, "参数类型错误"),
	PARAM_NOT_COMPLETE(100004, "参数缺失"),

	/* 用户错误：200001-299999*/
	USER_NOT_LOGGED_IN(200001, "用户未登录"),
	USER_LOGIN_ERROR(200002, "账号不存在或密码错误"),
	USER_ACCOUNT_FORBIDDEN(200003, "账号已被禁用"),
	USER_NOT_EXIST(200004, "用户不存在"),
	USER_HAS_EXISTED(200005, "用户已存在"),


	/* 系统错误：300001-39999 */
	SYSTEM_INNER_ERROR(300001, "系统异常，请联系管理员"),
	SYSTEM_BUSY_ERROR(300002, "系统繁忙，请稍后重试"),

	/* 接口错误：400001-49999 */
	INTERFACE_INNER_INVOKE_ERROR(400001, "内部系统接口调用异常"),
	INTERFACE_OUTTER_INVOKE_ERROR(400002, "外部系统接口调用异常"),
	INTERFACE_FORBID_VISIT(400003, "该接口禁止访问"),
	INTERFACE_ADDRESS_INVALID(400004, "接口地址无效"),
	INTERFACE_REQUEST_TIMEOUT(400005, "接口请求超时"),
	INTERFACE_EXCEED_LOAD(400006, "接口负载过高"),

	/* 权限错误：700001-79999 */
	PERMISSION_NO_ACCESS(500001, "无访问权限"),
	RESOURCE_EXISTED(500002, "资源已存在"),
	RESOURCE_NOT_EXISTED(500003, "资源不存在"),

	/* 业务错误：600001-69999 */
	ORDER_NOT_EXIST(600001, "业务错误-订单不存在"),

	USER_NOT_SubScribe(600002, "用户未关注"),

	/* 数据错误：70001-799999 */
	DATA_NOT_FOUND(70001, "数据未找到"),
	DATA_IS_WRONG(70002, "数据有误"),
	DATA_ALREADY_EXISTED(70003, "数据已存在"),
	HAD_CHILDREN_NODES(70004, "存在子节点");

	private Integer code;

	private String message;

	ResultCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer code() {
		return this.code;
	}

	public String message() {
		return this.message;
	}

	public static String getMessage(String name) {
		for (ResultCode item : ResultCode.values()) {
			if (item.name().equals(name)) {
				return item.message;
			}
		}
		return name;
	}

	public static Integer getCode(String name) {
		for (ResultCode item : ResultCode.values()) {
			if (item.name().equals(name)) {
				return item.code;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.name();
	}

}
