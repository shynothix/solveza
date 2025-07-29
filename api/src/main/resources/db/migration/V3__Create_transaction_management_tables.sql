-- 取引管理テーブル
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'JPY',
    description TEXT,
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_executed_at ON transactions(executed_at);
