/*
 Navicat Premium Data Transfer

 Source Server         : fgw
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : 47.105.91.12:3306
 Source Schema         : mall-open

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 14/01/2021 18:00:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mall_cart
-- ----------------------------
DROP TABLE IF EXISTS `mall_cart`;
CREATE TABLE `mall_cart`  (
  `id` bigint(20) NOT NULL,
  `member_id` bigint(20) NOT NULL COMMENT '用户编号',
  `goods_id` bigint(20) NOT NULL COMMENT '商品编号',
  `number` int(11) NOT NULL COMMENT '数量',
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'app_id',
  `union_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'union_id',
  `distributor_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属分销商',
  `delivery_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1 物流配送 2到店自提',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `sku_attr` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku_attr属性',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物车' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mall_goods
-- ----------------------------
DROP TABLE IF EXISTS `mall_goods`;
CREATE TABLE `mall_goods`  (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '副标题',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品编码',
  `type_id` bigint(20) NOT NULL COMMENT '分类id',
  `parent_type_id` bigint(20) NOT NULL COMMENT '父分类id',
  `brand_id` bigint(20) NULL DEFAULT NULL COMMENT '品牌id',
  `goods_type` tinyint(4) NOT NULL COMMENT '（0-实物、1-虚拟）',
  `vr_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '虚拟物品类型:coupon-电子券',
  `vr_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '虚拟物品代码 第三方券',
  `stock` int(11) NOT NULL COMMENT '库存数量',
  `stock_warn` int(11) NULL DEFAULT NULL COMMENT '库存预警值',
  `audit_status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1待审核 2、审核通过 3审核失败',
  `on_un_sale` tinyint(4) NULL DEFAULT NULL COMMENT '（0-上架，1-下架）',
  `logo` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品logo',
  `proposed_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考价',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `share_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分享描述',
  `is_recommand` tinyint(4) NULL DEFAULT 1 COMMENT '是否推荐 0 推荐 1不推荐',
  `is_refund` tinyint(4) NULL DEFAULT NULL COMMENT '是否支持退款 0支持 1不支持',
  `freight` decimal(10, 2) NULL DEFAULT NULL COMMENT '统一运费',
  `weight` int(11) NULL DEFAULT NULL COMMENT '重量 默认为克',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '单位',
  `list_order` mediumint(9) NULL DEFAULT NULL COMMENT '排序',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mall_order
-- ----------------------------
DROP TABLE IF EXISTS `mall_order`;
CREATE TABLE `mall_order`  (
  `id` bigint(20) NOT NULL,
  `order_no` bigint(50) NOT NULL COMMENT '订单编号',
  `member_id` bigint(20) NOT NULL COMMENT '会员编号',
  `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品总价',
  `freight` decimal(10, 2) NULL DEFAULT NULL COMMENT '物流运费',
  `deduction_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '积分抵扣金额',
  `coupon_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠券抵扣金额',
  `coupon_attr` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用优惠券信息',
  `concessionary_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '后台手工优惠金额',
  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '总计金额',
  `actual_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实付金额',
  `use_credit` bigint(20) NULL DEFAULT NULL COMMENT '使用积分',
  `delivery_type` tinyint(4) NULL DEFAULT NULL COMMENT '1 物流配送 2到店自提',
  `recharge_balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '储值支付金额',
  `pay_type` tinyint(4) NULL DEFAULT NULL COMMENT '付款方式:1、在线付款 2、货到付款 3、线下支付',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '付款时间',
  `pay_status` tinyint(4) NULL DEFAULT NULL COMMENT '支付状态 0 已支付 1 未支付 2支付中',
  `order_status` tinyint(4) NOT NULL COMMENT '1待付款   2 待发货 3 待收货 4待评价 5申请退款 6 退款成功 7交易成功 8交易关闭',
  `close_reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单关闭原因',
  `receiver` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户openid',
  `app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'app_id',
  `union_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'union_id',
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信支付订单号',
  `distributor_id` bigint(20) NULL DEFAULT NULL COMMENT '所属分销员',
  `apply_refund_time` datetime(0) NULL DEFAULT NULL COMMENT '申请退款时间',
  `refund_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '退款单号',
  `refund_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '退款原因',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  `reserve_time` datetime(0) NULL DEFAULT NULL COMMENT '预订时间',
  `goods_type` tinyint(4) NULL DEFAULT NULL COMMENT '商品类型  0 实物 、1 虚拟 ',
  `business_type` tinyint(4) NULL DEFAULT NULL COMMENT '业务类型 1 普通订单 2 秒杀订单 3拼团订单',
  `client_type` tinyint(4) NULL DEFAULT NULL COMMENT '1微信公众号 2微信小程序 3PC 4android 5 ios',
  `original` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单来源',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `AK_un_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mall_order_delivery
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_delivery`;
CREATE TABLE `mall_order_delivery`  (
  `id` bigint(50) NOT NULL,
  `order_no` bigint(50) NOT NULL COMMENT '订单编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `member_id` bigint(50) NOT NULL COMMENT '会员编号',
  `delivery_company_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流公司名称',
  `delivery_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流编号',
  `delivery_time` datetime(0) NULL DEFAULT NULL COMMENT '发货时间',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '市',
  `area` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区',
  `app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'app_id',
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'openid',
  `union_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'union_id',
  `address` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '送货地址',
  `list_order` mediumint(9) NULL DEFAULT NULL COMMENT '排序',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单物流信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mall_order_item
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_item`;
CREATE TABLE `mall_order_item`  (
  `id` bigint(20) NOT NULL,
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `order_no` bigint(50) NOT NULL COMMENT '订单编号',
  `member_id` bigint(20) NOT NULL COMMENT '会员编号',
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户openid',
  `app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'app_id',
  `union_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'union_id',
  `goods_id` bigint(20) NOT NULL COMMENT '商品编号',
  `number` int(11) NULL DEFAULT NULL COMMENT '数量',
  `cost_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品成本价',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `recharge_balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '储值支付金额',
  `concessionary_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠价格',
  `goods_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `goods_logo` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品logo',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `sku_attr` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku_attr属性',
  `distributor_id` bigint(20) NULL DEFAULT NULL COMMENT '所属分销员',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '0 正常 1 删除',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单项，订单包含的商品、订单详情' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
