-- Bug #13 fix: 补全已完成订单的时间戳
-- 修复策略：已完成订单(order_status=4)的时间戳用逐级推算

USE fresh_trace_shop;

-- 1. 已支付(1)但无支付时间 → 用 create_time 补
UPDATE order_info SET pay_time = create_time
WHERE order_status >= 1 AND pay_time IS NULL AND create_time IS NOT NULL;

-- 2. 已发货(2) + 已完成(4) 但无发货时间 → 用支付时间+1天推算
UPDATE order_info SET delivery_time = DATE_ADD(COALESCE(pay_time, create_time), INTERVAL 1 DAY)
WHERE order_status >= 2 AND delivery_time IS NULL;

-- 3. 已收货(3) + 已完成(4) 但无收货时间 → 用发货时间+1天推算
UPDATE order_info SET receive_time = DATE_ADD(COALESCE(delivery_time, pay_time, create_time), INTERVAL 2 DAY)
WHERE order_status >= 3 AND receive_time IS NULL;

-- 4. 已完成(4) 但无完成时间 → 用收货时间+1天推算
UPDATE order_info SET finish_time = DATE_ADD(COALESCE(receive_time, delivery_time, pay_time, create_time), INTERVAL 1 DAY)
WHERE order_status = 4 AND finish_time IS NULL;

-- 验证
SELECT '=== Bug #13 修复后验证 ===' AS info;
SELECT order_status, COUNT(*) 数量,
       SUM(CASE WHEN pay_time IS NULL THEN 1 ELSE 0 END) 无支付时间,
       SUM(CASE WHEN delivery_time IS NULL THEN 1 ELSE 0 END) 无发货时间,
       SUM(CASE WHEN receive_time IS NULL THEN 1 ELSE 0 END) 无收货时间,
       SUM(CASE WHEN finish_time IS NULL THEN 1 ELSE 0 END) 无完成时间
FROM order_info WHERE deleted=0 GROUP BY order_status;