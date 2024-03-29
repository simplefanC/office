package com.simplefanc.office.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动入口
 */
@SpringBootApplication(scanBasePackages = {"com.simplefanc.office.news"})
public class OfficeNewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeNewsApplication.class, args);
    }
}
