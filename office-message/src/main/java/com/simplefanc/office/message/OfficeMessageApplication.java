package com.simplefanc.office.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动入口
 */
@SpringBootApplication(scanBasePackages = {"com.simplefanc.office.common", "com.simplefanc.office.message"})
public class OfficeMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeMessageApplication.class, args);
    }
}
