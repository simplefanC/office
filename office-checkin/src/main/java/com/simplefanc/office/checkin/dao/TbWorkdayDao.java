package com.simplefanc.office.checkin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplefanc.office.checkin.entity.TbWorkday;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
@Mapper
public interface TbWorkdayDao extends BaseMapper<TbWorkday> {
    /**
     * @return
     */
    Integer searchTodayIsWorkday();

    /**
     * @param param
     * @return
     */
    ArrayList<String> searchWorkdayInRange(HashMap param);

    List<String> searchWorkdayInMonth(String yearMonth);
}