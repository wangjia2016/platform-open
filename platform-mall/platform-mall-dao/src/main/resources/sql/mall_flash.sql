/*
  Flash-sale promotion / session / session-goods schema.
  Aligns with README §9: activity -> sessions -> multi goods (goods may belong to many sessions).
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `mall_flash_sessions_goods`;
DROP TABLE IF EXISTS `mall_flash_sessions`;
DROP TABLE IF EXISTS `mall_flash_promotion`;

CREATE TABLE `mall_flash_promotion` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `start_time` datetime(0) NOT NULL COMMENT '活动开始时间',
  `end_time` datetime(0) NOT NULL COMMENT '活动结束时间',
  `is_enable` tinyint(4) NOT NULL DEFAULT 1 COMMENT '0 不启用 1启用',
  `remark` varchar(200) NULL DEFAULT NULL COMMENT '描述',
  `list_order` mediumint(9) NULL DEFAULT 0 COMMENT '排序',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `tenant_id` varchar(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='秒杀活动';

CREATE TABLE `mall_flash_sessions` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '场次名称',
  `start_time` datetime(0) NOT NULL COMMENT '场次开始时间',
  `end_time` datetime(0) NOT NULL COMMENT '场次结束时间',
  `flash_promotion_id` bigint(20) NULL DEFAULT NULL COMMENT '关联活动',
  `buy_limit` int(11) NULL DEFAULT NULL COMMENT '限购数量',
  `is_enable` tinyint(4) NOT NULL DEFAULT 1 COMMENT '0 不启用 1启用',
  `remark` varchar(200) NULL DEFAULT NULL COMMENT '描述',
  `list_order` mediumint(9) NULL DEFAULT 0 COMMENT '排序',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `tenant_id` varchar(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_flash_promotion_id` (`flash_promotion_id`) USING BTREE,
  KEY `idx_start_end` (`start_time`,`end_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='秒杀场次';

CREATE TABLE `mall_flash_sessions_goods` (
  `id` bigint(20) NOT NULL,
  `sessions_id` bigint(20) NOT NULL COMMENT '秒杀场次id',
  `goods_id` bigint(20) NOT NULL COMMENT '商品编号',
  `flash_price` decimal(10,2) NULL DEFAULT NULL COMMENT '秒杀价',
  `flash_stock` int(11) NULL DEFAULT NULL COMMENT '场次库存(可选,空则用商品库存)',
  `list_order` mediumint(9) NULL DEFAULT 0 COMMENT '排序',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `tenant_id` varchar(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_session_goods` (`sessions_id`,`goods_id`) USING BTREE,
  KEY `idx_goods_id` (`goods_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='秒杀场次关联商品';

SET FOREIGN_KEY_CHECKS = 1;
