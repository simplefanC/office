package com.simplefanc.office.message.task;

import com.rabbitmq.client.*;
import com.simplefanc.office.message.entity.MessageEntity;
import com.simplefanc.office.message.entity.MessageRefEntity;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程任务类
 *
 * @author chenfan
 */
@Slf4j
@Component
public class MessageTask {
    @Autowired
    private ConnectionFactory factory;
    @Autowired
    private MessageService messageService;

    /**
     * 同步向rabbitmq发送消息
     *
     * @param topic  主题
     * @param entity 消息对象
     */
    public void send(String topic, MessageEntity entity) {
        //向MongoDB保存消息数据，返回消息ID
        String id = messageService.insertMessage(entity);
        //向RabbitMQ发送消息
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()
        ) {//自动关闭
            //1.连接到某个Topic 2.消息是否持久化 3.排他（是否允许其他消费者同时连接到该队列） 4.是否自动删除队列（没有消费者连接时） 5.参数
            channel.queueDeclare(topic, true, false, false, null);
            //存放属性数据
            HashMap header = new HashMap(1);
            header.put("messageId", id);
            //创建AMQP协议参数对象，添加附加属性
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(header).build();
            //参数1为路由
            channel.basicPublish("", topic, properties, entity.getMsg().getBytes());
            log.debug("消息发送成功");
        } catch (Exception e) {
            log.error("执行异常", e);
            throw new EmosException("向MQ发送消息失败");
        }
    }

    /**
     * 异步执行发送消息
     * 发布公告消息的时候就不占用数据库正常的CRUD操作
     *
     * @param topic  主题
     * @param entity
     */
    @Async
    public void sendAsync(String topic, MessageEntity entity) {
        send(topic, entity);
    }

    /**
     * 同步接收数据
     *
     * @param topic 主题
     * @return 接收消息数量
     */
    public Integer receive(String topic) {
        int i = 0;
        try (//接收消息数据
             Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 从队列中获取消息，不自动确认
            channel.queueDeclare(topic, true, false, false, null);
            //Topic中有多少条数据未知，所以使用死循环接收数据，直到接收不到消息，退出死循环
            while (true) {
                //创建响应接收数据，禁止自动发送Ack应答
                GetResponse response = channel.basicGet(topic, false);
                if (response != null) {
                    AMQP.BasicProperties properties = response.getProps();
                    //获取附加属性对象
                    Map<String, Object> header = properties.getHeaders();
                    String messageId = header.get("messageId").toString();
                    //获取消息正文
                    byte[] body = response.getBody();
                    String message = new String(body);
                    log.debug("从RabbitMQ接收的消息：" + message);
                    MessageRefEntity entity = new MessageRefEntity();
                    entity.setMessageId(messageId);
                    entity.setReceiverId(Integer.parseInt(topic));
                    entity.setReadFlag(false);
                    entity.setLastFlag(true);
                    //把消息存储在MongoDB中
                    messageService.insertRef(entity);
                    //数据保存到MongoDB后，才发送Ack应答，让Topic删除这条消息
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag, false);
                    i++;
                } else {
                    break; //接收不到消息，则退出死循环
                }
            }
        } catch (Exception e) {
            log.error("执行异常", e);
        }
        return i;
    }

    /**
     * 从rabbitmq异步执行接收数据
     *
     * @param topic
     * @return
     */
    @Async
    public Integer receiveAysnc(String topic) {
        return receive(topic);
    }

    /**
     * 同步删除消息队列
     *
     * @param topic 主题
     */
    public void deleteQueue(String topic) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDelete(topic);
            log.debug("消息队列成功删除");
        } catch (Exception e) {
            log.error("删除队列失败", e);
            throw new EmosException("删除队列失败");
        }
    }

    /**
     * 异步删除消息队列
     *
     * @param topic 主题
     */
    @Async
    public void deleteQueueAsync(String topic) {
        deleteQueue(topic);
    }
}