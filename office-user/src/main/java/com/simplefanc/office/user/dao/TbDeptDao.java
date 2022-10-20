package com.simplefanc.office.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplefanc.office.user.entity.TbDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
@Mapper
public interface TbDeptDao extends BaseMapper<TbDept> {
    /**
     * @param keyword
     * @return
     */
    ArrayList<HashMap> searchDeptMembers(String keyword);

    List<TbDept> searchAllDept();

    int insertDept(TbDept entity);

    int updateDept(TbDept entity);
}