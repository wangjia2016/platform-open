/*
  Demo seed for flash-sale activity / session / session-goods.
  Run AFTER mall_flash.sql (schema) and after you have at least one row in mall_goods.

  Default links session goods to goods_id = 1. Change @demo_goods_id if needed.
  Times are relative to NOW() so the demo session stays "ongoing" after import.
*/

SET NAMES utf8mb4;

SET @demo_goods_id := 1;
SET @promo_id := 900001;
SET @session_id := 900002;
SET @link_id := 900003;

INSERT INTO `mall_flash_promotion` (
  `id`, `name`, `start_time`, `end_time`, `is_enable`, `remark`, `list_order`, `is_delete`, `create_user`, `tenant_id`
) VALUES (
  @promo_id,
  'Demo Flash Promotion',
  DATE_SUB(NOW(), INTERVAL 1 DAY),
  DATE_ADD(NOW(), INTERVAL 7 DAY),
  1,
  'Seed data for local / OSS demo',
  1,
  0,
  'seed',
  '0'
) ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `start_time` = VALUES(`start_time`),
  `end_time` = VALUES(`end_time`),
  `is_enable` = 1,
  `is_delete` = 0;

INSERT INTO `mall_flash_sessions` (
  `id`, `name`, `start_time`, `end_time`, `flash_promotion_id`, `buy_limit`,
  `is_enable`, `remark`, `list_order`, `is_delete`, `create_user`, `tenant_id`
) VALUES (
  @session_id,
  'Demo Ongoing Session',
  DATE_SUB(NOW(), INTERVAL 1 HOUR),
  DATE_ADD(NOW(), INTERVAL 1 DAY),
  @promo_id,
  1,
  1,
  'Seed ongoing session',
  1,
  0,
  'seed',
  '0'
) ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `start_time` = VALUES(`start_time`),
  `end_time` = VALUES(`end_time`),
  `flash_promotion_id` = VALUES(`flash_promotion_id`),
  `is_enable` = 1,
  `is_delete` = 0;

INSERT INTO `mall_flash_sessions_goods` (
  `id`, `sessions_id`, `goods_id`, `flash_price`, `flash_stock`,
  `list_order`, `is_delete`, `create_user`, `tenant_id`
) VALUES (
  @link_id,
  @session_id,
  @demo_goods_id,
  9.90,
  100,
  1,
  0,
  'seed',
  '0'
) ON DUPLICATE KEY UPDATE
  `flash_price` = VALUES(`flash_price`),
  `flash_stock` = VALUES(`flash_stock`),
  `is_delete` = 0;
