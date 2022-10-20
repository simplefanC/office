package com.simplefanc.office.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.simplefanc.office.user.entity.TbDept;
import com.simplefanc.office.user.entity.TbUser;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.user.dao.TbDeptDao;
import com.simplefanc.office.user.dao.TbUserDao;
import com.simplefanc.office.user.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private TbDeptDao deptDao;
    @Autowired
    private TbUserDao userDao;

    @Override
    public List<TbDept> searchAllDept() {
        return deptDao.searchAllDept();
    }

    @Override
    public void insertDept(TbDept entity) {
        int row = deptDao.insertDept(entity);
        if (row != 1) {
            throw new EmosException("添加部门失败");
        }
    }

    @Override
    public void updateDept(TbDept entity) {
        int row = deptDao.updateDept(entity);
        if (row != 1) {
            throw new EmosException("修改部门失败");
        }
    }

    @Override
    public void deleteDeptById(Integer id) {
        Integer count = userDao.selectCount(new QueryWrapper<TbUser>().eq("dept_id", id).eq("status", 1));
        if (count > 0) {
            throw new EmosException("删除失败，该部门有" + count + "名在职员工");
        } else {
            if (deptDao.deleteById(id) == 0) {
                throw new EmosException("删除失败");
            }
        }
    }
}
