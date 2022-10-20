package com.simplefanc.office.checkin.dao;

import com.simplefanc.office.checkin.entity.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author chenfan
 */
@Mapper
public interface TbCheckinDao {
    /**
     * 查询当天是否已经签到
     *
     * @param param
     * @return
     */
    Integer haveCheckin(HashMap param);

    /**
     * 保存签到记录
     *
     * @param checkin
     */
    void insert(TbCheckin checkin);

    /**
     * 当天签到情况
     * @param userId
     * @return
     */
    HashMap searchTodayCheckin(int userId);

    /**
     * 员工请考勤日期总数
     * @param userId
     * @return
     */
    long searchCheckinDays(int userId);

    /**
     * @param param
     * @return
     */
    ArrayList<HashMap> searchWeekCheckin(HashMap param);
}