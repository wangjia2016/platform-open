package com.platform.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.platform.common.enums.deserializer.EnumJacksonDeserializer;

/**
 * ClassName: OrderStatusEnum.java
 * @Description: 订单状态常量enum
 * @Author: 王佳
 * @Date: 2022年2月26日
 * @Version:1.0
 * @Copyright:©
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = EnumJacksonDeserializer.class)
public enum OrderStatusEnum {
	TO_PAY("待付款",1),
	TO_DELIVERY("待发货",2),
	TO_RECEIVE("待收货",3),
	TO_COMMENT("待评价",4),
	TRADE_CLOSED("交易关闭",5),
	TRADE_FINISHED("交易结束",6);
	/**
	 * 常量名称
	 */
	private String type;

	/**
	 * 常量值
	 */
	@EnumValue
	private Integer typeValue;

	private OrderStatusEnum(String type, Integer typeValue) {
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
