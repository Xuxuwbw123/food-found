-- =========================================================
-- 批量修复 P2/P3 Bug（数据层）
-- =========================================================
USE fresh_trace_shop;

-- Bug #3: 9条已完成订单无支付时间（Bug #13 的子集，已修复，验证）
SELECT 'Bug #3' AS check_name, COUNT(*) AS remaining
FROM order_info WHERE order_status=4 AND pay_time IS NULL;

-- Bug #7: 溯源找不到商品 — 商品删除时级联标记溯源删除
-- 先查出哪些溯源关联的商品已被删除
SELECT 'Bug #7 - 溯源关联软删除商品' AS check_name, COUNT(*) AS orphan_count
FROM traceability t WHERE t.deleted=0 AND t.product_id IS NOT NULL
  AND t.product_id NOT IN (SELECT id FROM product WHERE deleted=0);

-- 修复：把孤立溯源的 deleted 置为 1
UPDATE traceability SET deleted = 1
WHERE deleted=0 AND product_id IS NOT NULL
  AND product_id NOT IN (SELECT id FROM product WHERE deleted=0);

-- Bug #8: 订单金额与明细求和差18.10元
-- 排查 ORD20250410000001 订单
SELECT 'Bug #8' AS check_name, o.id, o.order_no, o.total_amount, o.pay_amount,
       SUM(oi.price * oi.quantity) AS detail_sum
FROM order_info o
JOIN order_item oi ON oi.order_id = o.id
WHERE o.order_no = 'ORD20250410000001'
GROUP BY o.id, o.order_no, o.total_amount, o.pay_amount;

-- 如果金额确实对不上，修复
-- UPDATE order_info SET total_amount = (SELECT SUM(oi.price * oi.quantity) FROM order_item oi WHERE oi.order_id = order_info.id) WHERE order_no = 'ORD20250410000001';

-- Bug #9: 有机鸡蛋 farmer_id=0
SELECT 'Bug #9 - farmer_id=0' AS check_name, COUNT(*) AS count
FROM product WHERE farmer_id = 0 AND deleted = 0;

-- 把 farmer_id=0 的商品赋给一个真实农户（取第一个可用农户）
UPDATE product p
SET p.farmer_id = (SELECT id FROM farmer WHERE deleted=0 LIMIT 1)
WHERE p.farmer_id = 0 AND p.deleted = 0;

-- Bug #14: 农户档案关联消费者账号
SELECT 'Bug #14 - 消费者关联农户档案' AS check_name, COUNT(*) AS count
FROM farmer f LEFT JOIN sys_user u ON f.user_id = u.id
WHERE f.deleted=0 AND u.user_type != 2;

-- 修复：删掉重复的农户档案（保留最早的）
DELETE f1 FROM farmer f1
INNER JOIN farmer f2 ON f1.farmer_name = f2.farmer_name AND f1.id > f2.id
WHERE f1.deleted=0 AND f2.deleted=0;

-- 把消费者关联的农户档案的 user_id 改为一个真实农户
UPDATE farmer f
JOIN sys_user u ON f.user_id = u.id
SET f.user_id = (SELECT id FROM sys_user WHERE user_type=2 AND deleted=0 LIMIT 1)
WHERE f.deleted=0 AND u.user_type != 2;

-- Bug #21: 收藏已删除商品
SELECT 'Bug #21 - 收藏已删除商品' AS check_name, COUNT(*) AS count
FROM product_favorite pf
WHERE pf.product_id NOT IN (SELECT id FROM product WHERE deleted=0);

-- 修复：软删除这些收藏
UPDATE product_favorite pf
SET pf.deleted = 1
WHERE pf.product_id NOT IN (SELECT id FROM product WHERE deleted=0);

-- =========================================================
-- 验证
-- =========================================================
SELECT '=== 验证全部 ===' AS info;
SELECT 'Bug #3' AS c, COUNT(*) AS v FROM order_info WHERE order_status=4 AND pay_time IS NULL;
SELECT 'Bug #7' AS c, COUNT(*) AS v FROM traceability t WHERE t.deleted=0 AND t.product_id IS NOT NULL AND t.product_id NOT IN (SELECT id FROM product WHERE deleted=0);
SELECT 'Bug #9' AS c, COUNT(*) AS v FROM product WHERE farmer_id = 0 AND deleted = 0;
SELECT 'Bug #14' AS c, COUNT(*) AS v FROM farmer f LEFT JOIN sys_user u ON f.user_id=u.id WHERE f.deleted=0 AND u.user_type != 2;
SELECT 'Bug #21' AS c, COUNT(*) AS v FROM product_favorite pf WHERE pf.product_id NOT IN (SELECT id FROM product WHERE deleted=0);