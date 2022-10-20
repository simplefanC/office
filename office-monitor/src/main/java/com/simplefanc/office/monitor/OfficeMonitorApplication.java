package com.simplefanc.office.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动入口
 */
@SpringBootApplication
@EnableAdminServer
public class OfficeMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeMonitorApplication.class, args);
    }
}
