package com.y5neko.amiya;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AMIYA 应用主类
 * 扫描 Mapper 包
 */
@SpringBootApplication
@MapperScan("com.y5neko.amiya.mapper")
public class AMIYA {
    public static void main(String[] args) {
        SpringApplication.run(AMIYA.class, args);
    }
}
