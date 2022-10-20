package com.simplefanc.office.message.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author chenfan
 */
@Document(collection = "message_ref")
@Data
public class MessageRefEntity implements Serializable {
    @Id
    private String _id;
    /**
     * message记录的_id
     */
    @Indexed
    private String messageId;
    @Indexed
    private Integer receiverId;
    /**
     * 是否已读
     */
    @Indexed
    private Boolean readFlag;
    /**
     * 是否为新接收的消息
     */
    @Indexed
    private Boolean lastFlag;
}