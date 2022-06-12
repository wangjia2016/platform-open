package com.platform.mall.dao.threadpool.model.detail;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * DetailDto
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2022-06-11 22:10:55
 */
@ApiModel("Detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPoolConfigDetailDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@JsonSerialize(using  = ToStringSerializer.class)
	@ApiModelProperty("")
	private Long id;

	/**
	 * 线程池名称
	 */
	@ApiModelProperty("线程池名称")
	private String threadPoolName;

	/**
	 * 服务名称
	 */
	@ApiModelProperty("服务名称")
	private String serviceName;

	/**
	 * 核心线程数
	 */
	@ApiModelProperty("核心线程数")
	private Integer corePoolSize;

	/**
	 * 最大线程数
	 */
	@ApiModelProperty("最大线程数")
	private Integer maximumPoolSize;

	/**
	 * 队列类型- SynchronousQueue ArrayBlockingQueue LinkedBlockingQueue
	 */
	@ApiModelProperty("队列类型- SynchronousQueue ArrayBlockingQueue LinkedBlockingQueue")
	private String blockingQueueType;

	/**
	 * 队列大小
	 */
	@ApiModelProperty("队列大小")
	private Integer queueSize;

	/**
	 * 是否启用告警 0->告警 1->不告警
	 */
	@ApiModelProperty("是否启用告警 0->告警 1->不告警")
	private Integer enableAlert;

	/**
	 * 告警阈值
	 */
	@ApiModelProperty("告警阈值")
	private Integer alertThreshold;

	/**
	 * 告警临近值
	 */
	@ApiModelProperty("告警临近值")
	private Integer alertNearValue;

	/**
	 * 排序
	 */
	@ApiModelProperty("排序")
	private Integer listOrder;

	/**
	 * 0 正常 1 删除
	 */
	@ApiModelProperty("0 正常 1 删除")
	private Integer isDelete;

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


}
