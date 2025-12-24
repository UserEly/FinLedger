-- FinLedger 财务管理系统数据库初始化脚本
-- 创建时间: 2024
-- 数据库: MySQL 8.0+

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS finledger CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE finledger;

-- ============================================
-- 1. 用户信息表 (users)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    role VARCHAR(20) NOT NULL COMMENT '角色：ACCOUNTANT(会计), MANAGER(财务经理), BOSS(老板)',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- ============================================
-- 2. 交易记录表 (transactions)
-- ============================================
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '交易ID',
    date DATETIME NOT NULL COMMENT '交易日期',
    supplier_client VARCHAR(100) NOT NULL COMMENT '供应商/客户',
    project VARCHAR(100) DEFAULT NULL COMMENT '项目',
    due_date DATETIME DEFAULT NULL COMMENT '账期',
    product_service VARCHAR(100) DEFAULT NULL COMMENT '产品/服务',
    quantity INT DEFAULT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    tax_rate DECIMAL(5,2) DEFAULT NULL COMMENT '税率',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING(待过账), POSTED(已过账), PAID(已支付)',
    user_id INT DEFAULT NULL COMMENT '录入用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_date (date),
    INDEX idx_status (status),
    INDEX idx_user_id (user_id),
    INDEX idx_supplier_client (supplier_client),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录表';

-- ============================================
-- 3. 账户科目表 (accounts)
-- ============================================
CREATE TABLE IF NOT EXISTS accounts (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '科目ID',
    code VARCHAR(20) NOT NULL UNIQUE COMMENT '科目编码',
    name VARCHAR(50) NOT NULL COMMENT '科目名称',
    category VARCHAR(20) NOT NULL COMMENT '科目类别：ASSET(资产), LIABILITY(负债), EQUITY(权益), REVENUE(收入), EXPENSE(费用)',
    debit_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '借方金额',
    credit_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '贷方金额',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '货币类型',
    parent_id INT DEFAULT NULL COMMENT '父科目ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (code),
    INDEX idx_category (category),
    INDEX idx_parent_id (parent_id),
    FOREIGN KEY (parent_id) REFERENCES accounts(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户科目表';

-- ============================================
-- 4. 分录表 (entries)
-- ============================================
CREATE TABLE IF NOT EXISTS entries (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '分录ID',
    summary VARCHAR(255) NOT NULL COMMENT '摘要',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT(草稿), SUBMITTED(待审核), APPROVED(已审核), REJECTED(已拒绝)',
    transaction_id INT NOT NULL COMMENT '关联交易ID',
    user_id INT NOT NULL COMMENT '录入用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_date (created_date),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分录表';

-- ============================================
-- 5. 分录拆分表 (splits)
-- ============================================
CREATE TABLE IF NOT EXISTS splits (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '拆分ID',
    entry_id INT NOT NULL COMMENT '关联分录ID',
    account_id INT NOT NULL COMMENT '关联科目ID',
    quantity INT DEFAULT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
    debit_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '借方金额',
    credit_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '贷方金额',
    tax_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '税额',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_entry_id (entry_id),
    INDEX idx_account_id (account_id),
    FOREIGN KEY (entry_id) REFERENCES entries(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分录拆分表';

-- ============================================
-- 6. 支付表 (payments)
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '支付ID',
    transaction_id INT NOT NULL COMMENT '关联交易ID',
    account_id INT NOT NULL COMMENT '关联科目ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_date DATETIME DEFAULT NULL COMMENT '支付日期',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING(待审批), APPROVED(已审批), REJECTED(已拒绝), PAID(已支付)',
    approved_by INT DEFAULT NULL COMMENT '审批人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_account_id (account_id),
    INDEX idx_approved_by (approved_by),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员账户（密码：admin123，需要BCrypt加密）
-- 实际使用时，请通过注册接口创建用户，这里仅作示例
-- INSERT INTO users (username, password, role, email) VALUES 
-- ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8p2wO', 'BOSS', 'admin@finledger.com');

-- 插入基础会计科目（示例）
INSERT INTO accounts (code, name, category, currency) VALUES
-- 资产类
('1001', '库存现金', 'ASSET', 'CNY'),
('1002', '银行存款', 'ASSET', 'CNY'),
('1012', '其他货币资金', 'ASSET', 'CNY'),
('1101', '交易性金融资产', 'ASSET', 'CNY'),
('1121', '应收票据', 'ASSET', 'CNY'),
('1122', '应收账款', 'ASSET', 'CNY'),
('1123', '预付账款', 'ASSET', 'CNY'),
('1221', '其他应收款', 'ASSET', 'CNY'),
('1401', '材料采购', 'ASSET', 'CNY'),
('1405', '库存商品', 'ASSET', 'CNY'),
('1601', '固定资产', 'ASSET', 'CNY'),
('1602', '累计折旧', 'ASSET', 'CNY'),
-- 负债类
('2001', '短期借款', 'LIABILITY', 'CNY'),
('2201', '应付票据', 'LIABILITY', 'CNY'),
('2202', '应付账款', 'LIABILITY', 'CNY'),
('2203', '预收账款', 'LIABILITY', 'CNY'),
('2211', '应付职工薪酬', 'LIABILITY', 'CNY'),
('2221', '应交税费', 'LIABILITY', 'CNY'),
('2231', '应付利息', 'LIABILITY', 'CNY'),
('2232', '应付股利', 'LIABILITY', 'CNY'),
('2241', '其他应付款', 'LIABILITY', 'CNY'),
-- 权益类
('4001', '实收资本', 'EQUITY', 'CNY'),
('4002', '资本公积', 'EQUITY', 'CNY'),
('4101', '盈余公积', 'EQUITY', 'CNY'),
('4103', '本年利润', 'EQUITY', 'CNY'),
('4104', '利润分配', 'EQUITY', 'CNY'),
-- 收入类
('6001', '主营业务收入', 'REVENUE', 'CNY'),
('6051', '其他业务收入', 'REVENUE', 'CNY'),
('6101', '公允价值变动损益', 'REVENUE', 'CNY'),
('6111', '投资收益', 'REVENUE', 'CNY'),
('6301', '营业外收入', 'REVENUE', 'CNY'),
-- 费用类
('6401', '主营业务成本', 'EXPENSE', 'CNY'),
('6402', '其他业务成本', 'EXPENSE', 'CNY'),
('6403', '税金及附加', 'EXPENSE', 'CNY'),
('6601', '销售费用', 'EXPENSE', 'CNY'),
('6602', '管理费用', 'EXPENSE', 'CNY'),
('6603', '财务费用', 'EXPENSE', 'CNY'),
('6701', '资产减值损失', 'EXPENSE', 'CNY'),
('6711', '营业外支出', 'EXPENSE', 'CNY'),
('6801', '所得税费用', 'EXPENSE', 'CNY')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ============================================
-- 视图创建（可选）
-- ============================================

-- 创建交易汇总视图
CREATE OR REPLACE VIEW v_transaction_summary AS
SELECT 
    t.id,
    t.date,
    t.supplier_client,
    t.total_amount,
    t.status,
    u.username AS created_by,
    COUNT(e.id) AS entry_count
FROM transactions t
LEFT JOIN users u ON t.user_id = u.id
LEFT JOIN entries e ON t.id = e.transaction_id
GROUP BY t.id, t.date, t.supplier_client, t.total_amount, t.status, u.username;

-- 创建分录汇总视图
CREATE OR REPLACE VIEW v_entry_summary AS
SELECT 
    e.id,
    e.summary,
    e.total_amount,
    e.status,
    e.created_date,
    t.supplier_client,
    u.username AS created_by,
    SUM(s.debit_amount) AS total_debit,
    SUM(s.credit_amount) AS total_credit
FROM entries e
LEFT JOIN transactions t ON e.transaction_id = t.id
LEFT JOIN users u ON e.user_id = u.id
LEFT JOIN splits s ON e.id = s.entry_id
GROUP BY e.id, e.summary, e.total_amount, e.status, e.created_date, t.supplier_client, u.username;

-- ============================================
-- 存储过程（可选）
-- ============================================

-- 验证分录借贷平衡的存储过程
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_validate_entry_balance(IN entry_id INT)
BEGIN
    SELECT 
        entry_id,
        SUM(debit_amount) AS total_debit,
        SUM(credit_amount) AS total_credit,
        CASE 
            WHEN ABS(SUM(debit_amount) - SUM(credit_amount)) < 0.01 THEN 'BALANCED'
            ELSE 'UNBALANCED'
        END AS balance_status
    FROM splits
    WHERE entry_id = entry_id
    GROUP BY entry_id;
END //
DELIMITER ;

-- ============================================
-- 完成
-- ============================================
SELECT 'Database initialization completed successfully!' AS message;

