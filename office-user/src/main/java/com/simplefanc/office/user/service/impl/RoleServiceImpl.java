package com.simplefanc.office.user.service.impl;

import cn.hutool.json.JSONObject;
import com.simplefanc.office.user.entity.TbRole;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.user.dao.TbRoleDao;
import com.simplefanc.office.user.dao.TbUserDao;
import com.simplefanc.office.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author chenfan
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private TbRoleDao roleDao;
    @Autowired
    private TbUserDao userDao;

    @Override
    public ArrayList<HashMap> searchRoleOwnPermission(int id) {
        ArrayList<HashMap> list = roleDao.searchRoleOwnPermission(id);
        list = handleData(list);
        return list;
    }

    /**
     * 将查询结果按照模块名称分组
     *
     * @param list
     * @return
     */
    private ArrayList<HashMap> handleData(ArrayList<HashMap> list) {
        ArrayList permsList = new ArrayList();
        ArrayList actionList = new ArrayList();
        HashSet set = new HashSet();
        HashMap data;
        for (HashMap map : list) {
            long permissionId = (Long) map.get("id");
            String moduleName = (String) map.get("moduleName");
            String actionName = (String) map.get("actionName");
            String selected = map.get("selected").toString();
            if (set.contains(moduleName)) {
                JSONObject json = new JSONObject();
                json.set("id", permissionId);
                json.set("actionName", actionName);
                json.set("selected", "1".equals(selected));
                actionList.add(json);
            } else {
                set.add(moduleName);
                data = new HashMap(2);
                data.put("moduleName", moduleName);
                actionList = new ArrayList();
                JSONObject json = new JSONObject();
                json.set("id", permissionId);
                json.set("actionName", actionName);
                json.set("selected", "1".equals(selected));
                actionList.add(json);
                data.put("action", actionList);
                permsList.add(data);
            }
        }
        return permsList;
    }

    @Override
    public ArrayList<HashMap> searchAllPermission() {
        ArrayList<HashMap> list = roleDao.searchAllPermission();
        list = handleData(list);
        return list;
    }

    @Override
    public void insertRole(TbRole role) {
        int row = roleDao.insertRole(role);
        if (row != 1) {
            throw new EmosException("添加角色失败");
        }
    }

    @Override
    public void updateRolePermissions(TbRole role) {
        int row = roleDao.updateRolePermissions(role);
        if (row != 1) {
            throw new EmosException("修改角色失败");
        }
    }

    @Override
    public List<TbRole> searchAllRole() {
        return roleDao.searchAllRole();
    }

    @Override
    public void deleteRoleById(Integer id) {
        Integer count = userDao.searchCountByRoleId(id);
        if (count > 0) {
            throw new EmosException("删除失败，有" + count + "名在职员工拥有该角色");
        } else {
            if (roleDao.deleteById(id) == 0) {
                throw new EmosException("删除失败");
            }
        }
    }

}