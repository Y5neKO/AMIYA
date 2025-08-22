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
    password VARCHAR(255) NOT NULL,
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

-- =====================
-- 漏洞特征库（支持多种验证方式，改进普通库多条条件）
-- =====================
CREATE TABLE vuln_signatures (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,                 -- 漏洞名称/签名名称
    vuln_type VARCHAR(64) NOT NULL,             -- 漏洞类型 (SQLi/XSS/命令执行/弱口令/端口服务等)
    cve_id VARCHAR(32),                         -- CVE编号
    cvss_score NUMERIC(3,1),                    -- CVSS评分

    -- 漏洞库类型
    signature_type VARCHAR(32) NOT NULL,        -- 普通库/文件库/插件库: db/file/jar

    -- 数据库型漏洞库字段
    protocol VARCHAR(16),                        -- 协议: http/tcp/udp
    method VARCHAR(8),                           -- 请求方法
    request_url VARCHAR(512),                    -- 请求URL/路径
    request_headers JSONB,                       -- 请求头
    request_body TEXT,                           -- 请求体
    verify_conditions JSONB,                     -- 响应验证条件数组，每条记录包含:
                                                 -- {
                                                 --   "type": "status|body|header|timing",
                                                 --   "value": "...",   -- 状态码、正则、JSON对象或时间
                                                 --   "operator": "equals|regex|gt|lt|contains"
                                                 -- }
                                                 -- 示例：
                                                 -- [
                                                 --      {"type":"status","operator":"equals","value":"200"},
                                                 --      {"type":"body","operator":"regex","value":"success"},
                                                 --      {"type":"header","operator":"contains","value":{"Server":"nginx"}},
                                                 --      {"type":"timing","operator":"lt","value":500}
                                                 -- ]

    verify_logic VARCHAR(16) DEFAULT 'AND',      -- 条件组合逻辑 AND/OR

    -- 文件型漏洞库字段
    template_file_path VARCHAR(512),             -- 存放验证模板的文件路径（JSON/YAML等）

    -- 插件型漏洞库字段
    plugin_jar_path VARCHAR(512),                -- 插件Jar文件路径
    plugin_class_name VARCHAR(255),              -- 插件主类
    plugin_config JSONB,                         -- 插件参数配置（可选）

    description TEXT,                            -- 漏洞描述
    reference_urls TEXT[],                        -- 参考链接数组
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 常用索引
CREATE INDEX idx_vuln_signatures_type ON vuln_signatures(vuln_type);
CREATE INDEX idx_vuln_signatures_cve ON vuln_signatures(cve_id);
CREATE INDEX idx_vuln_signatures_signature_type ON vuln_signatures(signature_type);
