package com.simplefanc.office.user.service;


import com.simplefanc.office.user.entity.TbRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
public interface RoleService {
    /**
     * @param id
     * @return
     */
    ArrayList<HashMap> searchRoleOwnPermission(int id);

    ArrayList<HashMap> searchAllPermission();

    void insertRole(TbRole role);

    void updateRolePermissions(TbRole role);

    /**
     * 查询所有的角色
     * @return
     */
    List<TbRole> searchAllRole();

    void deleteRoleById(Integer id);
}