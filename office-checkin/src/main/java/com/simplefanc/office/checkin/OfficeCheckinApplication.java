package com.simplefanc.office.checkin;

import com.simplefanc.office.checkin.service.SysConfigService;
import com.simplefanc.office.checkin.entity.SysConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * 应用启动入口
 */
@MapperScan({"com.simplefanc.office.checkin.dao"})
@SpringBootApplication(scanBasePackages = {"com.simplefanc.office.common", "com.simplefanc.office.checkin"})
@EnableFeignClients(basePackages = "com.simplefanc.office.common")
public class OfficeCheckinApplication {
    @Autowired
    private SysConfigService sysConfigService;

    @Value("${emos.image-folder}")
    private String imageFolder;

    public static void main(String[] args) {
        SpringApplication.run(OfficeCheckinApplication.class, args);
    }

    //    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate(){
//        RestTemplate restTemplate = new RestTemplate();
//        return restTemplate;
//    }


    /**
     * springboot初始化方法
     * springboot项目启动后自动执行
     */
    @PostConstruct
    public void init() {
        List<SysConfig> list = sysConfigService.getSystemConfig();
        sysConfigService.applySysConfigsToConstants(list);
        new File(imageFolder).mkdirs();
    }
}
