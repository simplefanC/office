package com.simplefanc.office.user.config;

import com.simplefanc.office.common.config.BaseSwaggerConfig;
import com.simplefanc.office.common.config.SwaggerProperties;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API相关配置
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.simplefanc.office.user.controller")
                .title("office用户系统")
                .description("office用户系统相关接口文档")
                .contactName("chenfan")
                .version("1.0")
                .enableSecurity(false)
                .build();
    }
}