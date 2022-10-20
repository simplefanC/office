package com.simplefanc.office.message.service;


import com.simplefanc.office.message.entity.MessageEntity;
import com.simplefanc.office.message.entity.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
public interface MessageService {
    /**
     * 向message插入数据
     *
     * @param entity
     * @return
     */
    String insertMessage(MessageEntity entity);

    /**
     * 向message_ref插入数据
     *
     * @param entity
     * @return
     */
    String insertRef(MessageRefEntity entity);

    /**
     * 查询未读消息的数量
     *
     * @param userId
     * @return
     */
    long searchUnreadCount(int userId);

    /**
     * 查询接收到最新消息的数量
     *
     * @param userId
     * @return
     */
    long searchLastCount(int userId);

    /**
     * 查询分页数据
     *
     * @param userId
     * @param start
     * @param length
     * @return
     */
    List<HashMap> searchMessageByPage(int userId, long start, int length);

    /**
     * 根据id查询消息
     *
     * @param id
     * @return
     */
    HashMap searchMessageById(String id);

    /**
     * 更新消息为已读状态
     *
     * @param id
     * @return
     */
    long updateUnreadMessage(String id);

    /**
     * 根据id删除消息
     *
     * @param id
     * @return
     */
    long deleteMessageRefById(String id);

    /**
     * 根据userid删除消息
     *
     * @param userId
     * @return
     */
    long deleteUserMessageRef(int userId);
}