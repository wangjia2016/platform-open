package com.platform.common.enums;

/**
 * ClassName: OnUnSaleEnum.java
 * @Description: 上下架状态常量enum
 * @Author: 王佳
 * @Date: 2021年09月19日
 * @Version:1.0
 * @Copyright:©
 */
public enum OnUnSaleEnum {

	ONSALE("上架",0),
	TO_ONSALE("待上架",2),
	UNSALE("下架",1);

	/**
	 * 常量名称
	 */
	private String type;

	/**
	 * 常量值
	 */
	private Integer typeValue;

	private OnUnSaleEnum(String type, Integer typeValue) {
		this.type = type;
		this.typeValue = typeValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(Integer typeValue) {
		this.typeValue = typeValue;
	}

}
