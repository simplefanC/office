package com.simplefanc.office.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 应用启动入口
 */
@MapperScan({"com.simplefanc.office.user.dao"})
@SpringBootApplication(scanBasePackages = {"com.simplefanc.office.common", "com.simplefanc.office.user"})
@EnableFeignClients(basePackages = "com.simplefanc.office.common")
public class OfficeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeUserApplication.class, args);
    }
}
