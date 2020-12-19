package com.platform.mall.dao.basic;

/**
 * ClassName: MallGoodsConstant.java
 * @Description: 商品常量enum
 * @Author: 王佳
 * @Date: 2020年11月13日
 * @Version:1.0
 * @Copyright:©
 */
public enum MallGoodsConstant {

	MATERIAL("实物商品",0),

	VIRTUAL("虚拟商品",1);

	/**
	 * 常量名称
	 */
	private String type;

	/**
	 * 常量值
	 */
	private Integer typeValue;

	private MallGoodsConstant(String type, Integer typeValue) {
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
