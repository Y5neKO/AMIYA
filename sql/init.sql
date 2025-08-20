-- =====================
-- 用户与角色表
-- =====================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================
-- 资产表
-- =====================
CREATE TABLE assets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    ip VARCHAR(50),
    domain VARCHAR(255),
    port INT,
    protocol VARCHAR(50),
    tags TEXT[],             -- 使用 text[] 存储标签
    owner_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================
-- 扫描任务表
-- =====================
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    asset_id BIGINT REFERENCES assets(id) ON DELETE CASCADE,
    scan_type VARCHAR(64) NOT NULL, -- full_scan / port_scan / vuln_scan / custom
    schedule_type VARCHAR(32) DEFAULT 'once', -- once / cron
    cron_expr VARCHAR(128),
    status VARCHAR(32) DEFAULT 'pending', -- pending / running / completed / failed
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================
-- 任务执行结果表
-- =====================
CREATE TABLE task_results (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    asset_id BIGINT REFERENCES assets(id),
    status VARCHAR(32) DEFAULT 'in_progress', -- in_progress / success / failed
    raw_result JSONB,  -- 原始扫描结果
    summary TEXT,      -- 结果摘要
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================
-- 漏洞表
-- =====================
CREATE TABLE vulnerabilities (
    id BIGSERIAL PRIMARY KEY,
    asset_id BIGINT REFERENCES assets(id),
    task_id BIGINT REFERENCES tasks(id),
    vuln_name VARCHAR(256) NOT NULL,
    severity VARCHAR(16) NOT NULL, -- critical/high/medium/low/info
    cve_id VARCHAR(32),
    cvss_score NUMERIC(3,1),
    description TEXT,
    evidence JSONB, -- 漏洞证据
    recommendation TEXT,
    discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vuln_severity ON vulnerabilities(severity);
CREATE INDEX idx_vuln_cve ON vulnerabilities(cve_id);

-- =====================
-- 报告表
-- =====================
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT REFERENCES tasks(id),
    file_path VARCHAR(512),
    format VARCHAR(16) DEFAULT 'pdf',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
