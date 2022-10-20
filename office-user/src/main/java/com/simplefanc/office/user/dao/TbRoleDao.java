package com.simplefanc.office.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplefanc.office.user.entity.TbRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
@Mapper
public interface TbRoleDao extends BaseMapper<TbRole> {
    /**
     * @param id
     * @return
     */
    ArrayList<HashMap> searchRoleOwnPermission(int id);

    /**
     *
     * @return
     */
    ArrayList<HashMap> searchAllPermission();

    /**
     *
     * @param role
     * @return
     */
    int insertRole(TbRole role);

    /**
     *
     * @param role
     * @return
     */
    int updateRolePermissions(TbRole role);

    /**
     * 查询所有角色
     * @return
     */
    List<TbRole> searchAllRole();
}