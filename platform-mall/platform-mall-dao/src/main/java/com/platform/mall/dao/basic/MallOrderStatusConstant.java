package com.platform.mall.dao.basic;

/**
 * ClassName: MallOrderStatusConstant.java
 * @Description: 商城订单状态常量enum
 * @Author: 王佳
 * @Date: 2020年11月13日
 * @Version:1.0
 * @Copyright:©
 */
public enum MallOrderStatusConstant {

	/***********************订单状态 start **************/
	TO_PAY("待付款",1),

	TO_DELIVERY("待发货",2),

	/**
	 * 已发货-待确认-待收货
	 * 发货之后状态变更
	 */
	TO_RECEIVE("待收货",3) ,

	/**
	 * 待评价：1、确认收货之后 2、核销之后
	 * */
	TO_COMMENT("待评价",4),

	APPLY_REFUND("申请退款",5),
	
	REFUNDED("退款完成",6),
	
	FAIL_REFUND("退款失败",7),

	TRADE_CLOSED("交易关闭",8),
	/**
	 * 已完成-交易结束
	 * */
	SUCCESS_TRADE("交易结束",9);

	/**
	 * 常量名称
	 */
	private String type;
	
	/**
	 * 常量值
	 */
	private Integer typeValue;

	private MallOrderStatusConstant(String type, Integer typeValue) {
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
