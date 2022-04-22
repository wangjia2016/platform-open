package com.platform.mall.dao.order.model.query;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 限时抢购场次-全局DetailDto
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2021-03-08 13:56:21
 */
@ApiModel("限时抢购场次-全局Detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallFlashSessionsDetailDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@JsonSerialize(using  = ToStringSerializer.class)
	@ApiModelProperty("")
	private Long id;

	/**
	 * 场次名称
	 */
	@ApiModelProperty("场次名称")
	private String name;

	/**
	 * 开始时间
	 */
	@ApiModelProperty("开始时间")
	private Date startTime;

	/**
	 * 结束时间
	 */
	@ApiModelProperty("结束时间")
	private Date endTime;

	/**
	 * 关联活动
	 */
	@ApiModelProperty("关联活动")
	@JsonSerialize(using  = ToStringSerializer.class)
	private Long flashPromotionId;

	/**
	 * 限购
	 */
	@ApiModelProperty("限购")
	private Integer buyLimit;

	/**
	 * 0 不启用  1启用
	 */
	@ApiModelProperty("0 启用  1不启用")
	private Integer isEnable;

	/**
	 * 描述
	 */
	@ApiModelProperty("描述")
	private String remark;

	/**
	 * 排序
	 */
	@ApiModelProperty("排序")
	private Integer listOrder;

	/**
	 * 0 正常 1 删除
	 */
	@ApiModelProperty("0 正常 1 删除")
	private Long isDelete;

	/**
	 * 创建人
	 */
	@ApiModelProperty("创建人")
	private String createUser;

	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private Date createTime;

	/**
	 * 修改人
	 */
	@ApiModelProperty("修改人")
	private String updateUser;

	/**
	 * 修改时间
	 */
	@ApiModelProperty("修改时间")
	private Date updateDatetime;

	/**
	 * 租户代码
	 */
	@ApiModelProperty("租户代码")
	private String tenantCode;

	/**
	 * 租户集团代码
	 */
	@ApiModelProperty("租户集团代码")
	private String tenantGroupCode;


}
