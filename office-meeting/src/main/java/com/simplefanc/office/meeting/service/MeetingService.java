package com.simplefanc.office.meeting.service;


import com.simplefanc.office.meeting.entity.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
public interface MeetingService {
    /**
     * @param entity
     */
    void insertMeeting(TbMeeting entity);

    /**
     * @param param
     * @return
     */
    ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    /**
     * @param id
     * @return
     */
    HashMap searchMeetingById(int id);

    /**
     * @param param
     */
    void updateMeetingInfo(HashMap param);

    /**
     * @param id
     */
    void deleteMeetingById(int id);

    /**
     * 工作流项目前15 min创建roomID
     * 通过uuid从redis拿到roomID
     * @param uuid
     * @return
     */
    Integer searchRoomIdByUUID(String uuid);

    /**
     * @param param
     * @return
     */
    List<String> searchUserMeetingInMonth(HashMap param);

    HashMap searchMeetingByInstanceId(String instanceId);

    void updateMeetingStatus(HashMap param);

    HashMap searchMeetingByUUID(String uuid);
}