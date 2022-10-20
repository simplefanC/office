package com.simplefanc.office.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author chenfan
 */
@Data
public class MessageEntity {
    private String uuid;
    /**
     * 发送者ID，就是用户ID。如果是系统自动发出，这个ID值是0
     */
    private Integer senderId;
    private String senderPhoto = "https://static-1258386385.cos.ap-beijing.myqcloud.com/img/System.jpg";
    private String senderName;
    private Date sendTime;
    private String msg;
}