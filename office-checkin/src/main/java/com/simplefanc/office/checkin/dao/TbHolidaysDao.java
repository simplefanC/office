package com.simplefanc.office.checkin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplefanc.office.checkin.entity.TbHolidays;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
@Mapper
public interface TbHolidaysDao  extends BaseMapper<TbHolidays> {
    /**
     * @return
     */
    Integer searchTodayIsHolidays();

    /**
     * @param param
     * @return
     */
    ArrayList<String> searchHolidaysInRange(HashMap param);

    List<String> searchHolidaysInMonth(String yearMonth);
}