CREATE TABLE IF NOT EXISTS shop (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL,
  category VARCHAR(40) NOT NULL,
  address VARCHAR(160) NOT NULL,
  avg_price DECIMAL(10,2) NOT NULL,
  score DECIMAL(3,1) NOT NULL,
  description VARCHAR(512) NOT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS voucher (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shop_id BIGINT NOT NULL,
  title VARCHAR(120) NOT NULL,
  pay_value DECIMAL(10,2) NOT NULL,
  actual_value DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  begin_at DATETIME NOT NULL,
  end_at DATETIME NOT NULL,
  version INT NOT NULL DEFAULT 0,
  INDEX idx_voucher_shop(shop_id)
);

CREATE TABLE IF NOT EXISTS voucher_order (
  id BIGINT PRIMARY KEY,
  voucher_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  created_at DATETIME NOT NULL,
  paid_at DATETIME NULL,
  closed_at DATETIME NULL,
  version INT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_user_voucher(user_id, voucher_id),
  INDEX idx_order_status_created(status, created_at)
);
