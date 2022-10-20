package com.simplefanc.office.message.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenfan
 */
@Data
@Document(collection = "message")
public class MessageEntity implements Serializable {
    @Id
    private String _id;
    @Indexed(unique = true)
    private String uuid;
    /**
     * 发送者ID，就是用户ID。如果是系统自动发出，这个ID值是0
     */
    @Indexed
    private Integer senderId;
    private String senderPhoto = "https://static-1258386385.cos.ap-beijing.myqcloud.com/img/System.jpg";
    private String senderName;
    @Indexed
    private Date sendTime;
    private String msg;
}