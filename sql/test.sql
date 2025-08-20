-- 角色
INSERT INTO roles (role_name, description) VALUES
('admin', '系统管理员'),
('user', '普通用户');

-- 用户
INSERT INTO users (username, password_hash, email, role_id, is_active) VALUES
('admin', 'hash_admin', 'admin@example.com', 1, TRUE),
('user1', 'hash_user1', 'user1@example.com', 2, TRUE);

-- 资产
INSERT INTO assets (name, ip, domain, port, protocol, tags, owner_id, status) VALUES
('测试资产1', '192.168.1.10', 'example.com', 80, 'tcp', ARRAY['web','production'], 2, 'active'),
('测试资产2', '192.168.1.11', 'test.local', 22, 'tcp', ARRAY['ssh','internal'], 2, 'active');

-- 扫描任务
INSERT INTO tasks (name, asset_id, scan_type, schedule_type, status, created_by) VALUES
('端口扫描任务1', 1, 'port_scan', 'once', 'pending', 2),
('漏洞扫描任务1', 2, 'vuln_scan', 'once', 'pending', 2);

-- 扫描结果
INSERT INTO task_results (task_id, asset_id, status, raw_result, summary, started_at, finished_at) VALUES
(1, 1, 'success', '{"open_ports":[80,443]}', '发现2个开放端口', NOW() - INTERVAL '10 minutes', NOW() - INTERVAL '9 minutes'),
(2, 2, 'success', '{"vulns_found":[{"name":"弱口令SSH","severity":"medium"}]}', '发现1个中危漏洞', NOW() - INTERVAL '5 minutes', NOW() - INTERVAL '4 minutes');

-- 漏洞
INSERT INTO vulnerabilities (asset_id, task_id, vuln_name, severity, cve_id, cvss_score, description, evidence, recommendation) VALUES
(2, 2, '弱口令SSH', 'medium', 'CVE-2023-0001', 5.0, 'SSH服务存在弱口令风险', '{"attempted_logins":["root:123456","admin:admin"]}', '请更改弱口令并启用SSH密钥认证');

-- 报告
INSERT INTO reports (task_id, file_path, format, generated_at) VALUES
(1, '/reports/port_scan_task1.pdf', 'pdf', NOW()),
(2, '/reports/vuln_scan_task1.pdf', 'pdf', NOW());
