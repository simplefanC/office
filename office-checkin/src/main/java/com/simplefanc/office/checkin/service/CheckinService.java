package com.simplefanc.office.checkin.service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author chenfan
 */
public interface CheckinService {
    /**
     * 封装检测是否可以签到
     *
     * @param userId
     * @param date
     * @return
     */
    String validCanCheckIn(int userId, String date);

    /**
     * 签到
     *
     * @param param
     */
    String checkin(HashMap<String, Object> param);

    /**
     * 用签到照片创建人脸模型数据
     *
     * @param userId
     * @param path
     */
    String createFaceModel(int userId, String path);

    /**
     * @param userId
     * @return
     */
    HashMap searchTodayCheckin(int userId);

    /**
     * @param userId
     * @return
     */
    long searchCheckinDays(int userId);

    /**
     * @param param
     * @return
     */
    ArrayList<HashMap> searchWeekCheckin(HashMap param);

    /**
     * 查询月考勤
     * @param param
     * @return
     */
    ArrayList<HashMap> searchMonthCheckin(HashMap param);

}
