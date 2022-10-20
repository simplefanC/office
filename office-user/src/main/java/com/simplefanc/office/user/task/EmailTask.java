package com.simplefanc.office.user.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 采用异步发送邮件
 *
 * @author chenfan
 */
@Component
public class EmailTask implements Serializable {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${emos.email.system}")
    private String mailbox;

    @Async
    public void sendAsync(SimpleMailMessage message) {
        message.setFrom(mailbox);
        //抄送给自己
//        message.setCc(mailbox);
        javaMailSender.send(message);
    }

    @Async
    public void test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        System.out.println("异步任务执行over");
    }
}
