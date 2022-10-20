package com.simplefanc.office.user.service;

import com.simplefanc.office.user.entity.TbDept;

import java.util.List;

public interface DeptService {
    List<TbDept> searchAllDept();

    void insertDept(TbDept entity);

    void updateDept(TbDept entity);

    void deleteDeptById(Integer id);
}
