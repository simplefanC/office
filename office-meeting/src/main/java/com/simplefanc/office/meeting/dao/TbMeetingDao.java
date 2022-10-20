package com.simplefanc.office.meeting.dao;

import com.simplefanc.office.meeting.entity.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
@Mapper
public interface TbMeetingDao {
    /**
     * @param entity
     * @return
     */
    int insertMeeting(TbMeeting entity);

    /**
     * @param param
     * @return
     */
    ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    /**
     * @param uuid
     * @return
     */
    boolean searchMeetingMembersInSameDept(String uuid);

    /**
     * @param map
     * @return
     */
    int updateMeetingInstanceId(HashMap map);

    /**
     * @param id
     * @return
     */
    HashMap searchMeetingById(int id);

    /**
     * @param id
     * @return
     */
    ArrayList<HashMap> searchMeetingMembers(int id);

    /**
     * @param param
     * @return
     */
    int updateMeetingInfo(HashMap param);

    /**
     * @param id
     * @return
     */
    int deleteMeetingById(int id);

    /**
     * @param param
     * @return
     */
    List<String> searchUserMeetingInMonth(HashMap param);

    HashMap searchMeetingByUUID(String uuid);

    HashMap searchMeetingByInstanceId(String instanceId);

    int updateMeetingStatus(HashMap param);
}