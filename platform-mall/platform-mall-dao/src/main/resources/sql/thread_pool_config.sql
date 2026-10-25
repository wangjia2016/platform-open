/*
  Thread pool config used by orderThreadPoolTuner at startup
  (see platform-mall-web .../config/init/ThreadPoolConfig.java).
  Matches ThreadPoolNameEnum: goods_pool / order_pool.
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `thread_pool_config` (
  `id` bigint(20) NOT NULL,
  `thread_pool_name` varchar(64) NOT NULL COMMENT '线程池名称(goods_pool/order_pool)',
  `service_name` varchar(64) NULL DEFAULT NULL COMMENT '服务名称',
  `core_pool_size` int(11) NOT NULL COMMENT '核心线程数',
  `maximum_pool_size` int(11) NOT NULL COMMENT '最大线程数',
  `blocking_queue_type` varchar(64) NULL DEFAULT 'ArrayBlockingQueue' COMMENT '队列类型',
  `queue_size` int(11) NULL DEFAULT 1000 COMMENT '队列大小',
  `enable_alert` tinyint(4) NULL DEFAULT 1 COMMENT '0 告警 1 不告警',
  `alert_threshold` int(11) NULL DEFAULT NULL COMMENT '告警阈值',
  `alert_near_value` int(11) NULL DEFAULT NULL COMMENT '告警临近值',
  `list_order` mediumint(9) NULL DEFAULT 0 COMMENT '排序',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_thread_pool_name` (`thread_pool_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='线程池配置';

INSERT INTO `thread_pool_config` (
  `id`, `thread_pool_name`, `service_name`, `core_pool_size`, `maximum_pool_size`,
  `blocking_queue_type`, `queue_size`, `enable_alert`, `list_order`, `is_delete`, `create_user`
) VALUES
  (1, 'order_pool', 'platform-mall', 4, 100, 'ArrayBlockingQueue', 1000, 1, 1, 0, 'seed'),
  (2, 'goods_pool', 'platform-mall', 2, 50, 'ArrayBlockingQueue', 500, 1, 2, 0, 'seed')
ON DUPLICATE KEY UPDATE
  `core_pool_size` = VALUES(`core_pool_size`),
  `maximum_pool_size` = VALUES(`maximum_pool_size`),
  `queue_size` = VALUES(`queue_size`),
  `is_delete` = 0;

SET FOREIGN_KEY_CHECKS = 1;
