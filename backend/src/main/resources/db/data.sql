INSERT INTO shop(id, name, category, address, avg_price, score, description) VALUES
(1, '巷口烤肉', '烧烤', '西安市雁塔区科技路88号', 68.00, 4.8, '夜宵高峰热门商家，主打烤肉套餐'),
(2, '麦田轻食', '轻食', '西安市长安区西电南校区东门', 36.00, 4.7, '低脂健康套餐和饮品');

INSERT INTO voucher(id, shop_id, title, pay_value, actual_value, stock, begin_at, end_at, version) VALUES
(1, 1, '100元烧烤套餐秒杀券', 39.90, 100.00, 100, '2026-01-01 00:00:00', '2027-12-31 23:59:59', 0),
(2, 2, '轻食双人餐秒杀券', 29.90, 68.00, 80, '2026-01-01 00:00:00', '2027-12-31 23:59:59', 0);

INSERT INTO voucher_order(id, voucher_id, user_id, status, created_at, version) VALUES
(10001, 1, 7, 'PENDING_PAYMENT', NOW(), 0);
