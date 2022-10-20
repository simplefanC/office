package com.simplefanc.office.message.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 *
 * @author chenfan
 */
@Configuration
public class RabbitMQConfig {
    @Value("${emos.rabbitmq.host}")
    private String host;
    @Value("${emos.rabbitmq.port}")
    private Integer port;
    /**
     * 同步收发消息 需要该配置；若异步仅yml配置即可
     *
     * @return
     */
    @Bean
    public ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        //Linux主机的IP地址
        factory.setHost(host);
        //RabbitMQ端口号
        factory.setPort(port);
        return factory;
    }
}