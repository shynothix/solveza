-- アカウント管理テーブル
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    requester_id UUID NOT NULL,
    payer_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_accounts_requester_payer ON accounts(requester_id, payer_id);
