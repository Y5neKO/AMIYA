package com.y5neko.amiya.test;

import com.y5neko.amiya.entity.*;
import com.y5neko.amiya.mapper.*;
import com.y5neko.amiya.util.MiscUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class DatabaseIntegrationTest {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AssetMapper assetsMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskResultMapper taskResultsMapper;

    @Autowired
    private VulnerabilityMapper vulnerabilitiesMapper;

    @Autowired
    private ReportMapper reportsMapper;

    @Test
    void testRolesAndUsers() {
        // 插入 Role
        Role role = new Role();
        role.setRoleName("admin" + MiscUtils.getRamdomStr(6));
        role.setDescription("管理员");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.insert(role);

        // 插入 User
        User user = new User();
        user.setUsername("tester" + MiscUtils.getRamdomStr(6));
        user.setPassword("123456");
        user.setEmail("tester@example.com");
        user.setRoleId(role.getId());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        // 查询
        System.out.println("Role: " + roleMapper.selectById(role.getId()));
        System.out.println("User: " + userMapper.selectById(user.getId()));
    }

    @Test
    void testAssets() {
        Asset asset = new Asset();
        asset.setName("测试资产");
        asset.setIp("192.168.1.1");
        asset.setDomain("example.com");
        asset.setPort(80);
        asset.setProtocol("http");
        asset.setTags(new String[]{"web","test"});
        asset.setOwnerId(1L);
        asset.setStatus("active");
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());

        assetsMapper.insert(asset);

        Asset dbAsset = assetsMapper.selectById(asset.getId());
        System.out.println("Asset: " + "测试资产SpringBoot, tags: " + String.join(",", dbAsset.getTags()));
    }

    @Test
    void testTasksAndResults() {
        Task task = new Task();
        task.setName("全量扫描");
        task.setAssetId(1L);
        task.setScanType("full_scan");
        task.setScheduleType("once");
        task.setStatus("pending");
        task.setCreatedBy(1L);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        taskMapper.insert(task);

        TaskResult results = new TaskResult();
        results.setTaskId(task.getId());
        results.setAssetId(1L);
        results.setStatus("in_progress");
        Map<String,Object> rawResult = new HashMap<>();
        rawResult.put("port", 80);
        results.setRawResult(rawResult);
        results.setSummary("扫描中");
        results.setStartedAt(LocalDateTime.now());
        results.setFinishedAt(LocalDateTime.now());
        results.setCreatedAt(LocalDateTime.now());

        taskResultsMapper.insert(results);

        System.out.println("Task: " + taskMapper.selectById(task.getId()));
        System.out.println("TaskResults: " + taskResultsMapper.selectById(results.getId()));
    }

    @Test
    void testVulnerabilities() {
        Vulnerability vuln = new Vulnerability();
        vuln.setAssetId(1L);
        vuln.setTaskId(1L);
        vuln.setVulnName("测试漏洞");
        vuln.setSeverity("high");
        vuln.setCveId("CVE-2025-1234");
        vuln.setCvssScore(7.5);
        vuln.setDescription("描述信息");
        Map<String,Object> evidence = new HashMap<>();
        evidence.put("file","/etc/passwd");
        vuln.setEvidence(evidence);
        vuln.setRecommendation("修复方法");
        vuln.setDiscoveredAt(LocalDateTime.now());
        vuln.setCreatedAt(LocalDateTime.now());

        vulnerabilitiesMapper.insert(vuln);

        System.out.println("Vulnerability: " + vulnerabilitiesMapper.selectById(vuln.getId()));
    }

    @Test
    void testReports() {
        Report report = new Report();
        report.setTaskId(1L);
        report.setFilePath("/tmp/report.pdf");
        report.setFormat("pdf");
        report.setGeneratedAt(LocalDateTime.now());

        reportsMapper.insert(report);

        System.out.println("Report: " + reportsMapper.selectById(report.getId()));
    }
}
