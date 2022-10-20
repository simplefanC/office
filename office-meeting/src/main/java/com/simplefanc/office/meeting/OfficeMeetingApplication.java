package com.simplefanc.office.meeting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 应用启动入口
 */
@MapperScan({"com.simplefanc.office.meeting.dao"})
@SpringBootApplication(scanBasePackages = {"com.simplefanc.office.common", "com.simplefanc.office.meeting"})
@EnableFeignClients(basePackages = "com.simplefanc.office.common")
public class OfficeMeetingApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeMeetingApplication.class, args);
    }
}
