package com.simplefanc.office.checkin.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author chenfan
 */
@Mapper
public interface TbCityDao {
    /**
     * 根据城市名称查询到城市编号
     * @param city
     * @return
     */
    String searchCode(String city);
}