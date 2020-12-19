package com.platform.mall.dao.order.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: MallFlashOrderRequestQuery.java
 * @Description: 限时抢购-秒杀保存类型封装
 * @Author: 王佳
 * @Date: 2020年11月6日
 * @Version:1.0
 * @Copyright:©
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("限时抢购-秒杀订单提交参数")
public class MallFlashOrderRequestQuery extends MallOrderQuery {

	@ApiModelProperty("省")
	private String province;

	@ApiModelProperty("市")
	private String city;

	@ApiModelProperty("区")
	private String area;

	@ApiModelProperty("详细地址")
	private String address;
}
