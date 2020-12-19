package com.platform.mall.dao.order.model.detail;

import java.io.Serializable;
import java.util.List;
import com.platform.mall.dao.order.model.list.MallOrderItemListDto;
import com.platform.mall.dao.order.model.list.MallOrderListDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DetailDto
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:06:55
 */
@ApiModel("订单列表List")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MallOrderDetailDto extends MallOrderListDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("订单项")
	private List<MallOrderItemListDto> listMallOrderItem;

	/**
	 * 显示配送信息
	 * */
	@ApiModelProperty("物流配送信息")
	private MallOrderDeliveryDetailDto mallOrderDelivery;


}


