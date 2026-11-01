-- 已有库手工执行一次。新库建表脚本已经带上这些索引，不要重复执行。
-- 订单详情、取消回补按 order_id / order_no 查明细，会员订单按 member_id 查。

ALTER TABLE `mall_order`
  ADD INDEX `idx_member_id` (`member_id`);

ALTER TABLE `mall_order_item`
  ADD INDEX `idx_order_id` (`order_id`),
  ADD INDEX `idx_order_no` (`order_no`);

ALTER TABLE `mall_order_delivery`
  ADD INDEX `idx_order_id` (`order_id`);
