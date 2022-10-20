package com.simplefanc.office.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 转发规律：
 * 访问${GATEWAY_ URL}/{微服务X}/**  会转发到微服务X的/**路径
 */
@SpringBootApplication
public class OfficeGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeGatewayApplication.class, args);
    }
}
