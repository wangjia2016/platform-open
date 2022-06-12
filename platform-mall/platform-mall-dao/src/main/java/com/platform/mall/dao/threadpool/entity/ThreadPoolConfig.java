package com.platform.mall.dao.threadpool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2022-06-11 22:10:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("thread_pool_config")
public class ThreadPoolConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type=IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 线程池名称
	 */
	private String threadPoolName;

	/**
	 * 服务名称
	 */
	private String serviceName;

	/**
	 * 核心线程数
	 */
	private Integer corePoolSize;

	/**
	 * 最大线程数
	 */
	private Integer maximumPoolSize;

	/**
	 * 队列类型- SynchronousQueue ArrayBlockingQueue LinkedBlockingQueue
	 */
	private String blockingQueueType;

	/**
	 * 队列大小
	 */
	private Integer queueSize;

	/**
	 * 是否启用告警 0->告警 1->不告警
	 */
	private Integer enableAlert;

	/**
	 * 告警阈值
	 */
	private Integer alertThreshold;

	/**
	 * 告警临近值
	 */
	private Integer alertNearValue;

	/**
	 * 排序
	 */
	private Integer listOrder;

	/**
	 * 0 正常 1 删除
	 */
	@TableLogic
	private Integer isDelete;

	/**
	 * 创建人
	 */
	private String createUser;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改人
	 */
	private String updateUser;

	/**
	 * 修改时间
	 */
	private Date updateDatetime;

}
