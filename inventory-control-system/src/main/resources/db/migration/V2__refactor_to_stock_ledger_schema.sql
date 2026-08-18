-- 1. Removemos as tabelas antigas do modelo monolítico relacional
DROP TABLE IF EXISTS tb_products CASCADE;
DROP TABLE IF EXISTS tb_categories CASCADE;

-- 2. Tabela principal de Saldo Rígido de Estoque
CREATE TABLE tb_stock_balance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id VARCHAR(64) NOT NULL, -- Recebe o ID textual (_id / ObjectId) gerado pelo MongoDB
    sku VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    min_quantity INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0, -- Controle de concorrência otimista (Optimistic Locking)
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_stock_balance_product_id UNIQUE (product_id),
    CONSTRAINT uk_stock_balance_sku UNIQUE (sku)
);

-- 3. Tabela de Auditoria e Histórico Imutável de Movimentações (Ledger)
CREATE TABLE tb_stock_movement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_balance_id UUID NOT NULL,
    movement_type VARCHAR(20) NOT NULL, -- 'ENTRY', 'EXIT', 'ADJUSTMENT'
    quantity INT NOT NULL,
    previous_quantity INT NOT NULL,
    new_quantity INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_stock_movement_balance 
        FOREIGN KEY (stock_balance_id) 
        REFERENCES tb_stock_balance(id) 
        ON DELETE CASCADE
);

-- 4. Índice para otimizar consultas de histórico por produto
CREATE INDEX idx_stock_movement_balance ON tb_stock_movement(stock_balance_id);