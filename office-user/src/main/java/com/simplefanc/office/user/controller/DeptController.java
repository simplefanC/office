package com.simplefanc.office.user.controller;

import com.simplefanc.office.common.auth.Logical;
import com.simplefanc.office.common.auth.RequiresPermissions;
import com.simplefanc.office.common.util.R;
import com.simplefanc.office.user.controller.form.dept.DeleteDeptByIdForm;
import com.simplefanc.office.user.controller.form.dept.InsertDeptForm;
import com.simplefanc.office.user.controller.form.dept.UpdateDeptForm;
import com.simplefanc.office.user.entity.TbDept;
import com.simplefanc.office.user.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dept")
@Api("部门模块网络接口")
public class DeptController {
    @Autowired
    private DeptService deptService;

    @PostMapping("/searchAllDept")
    @ApiOperation("查询所有部门")
//    @RequiresPermissions(value = {"ROOT", "DEPT:SELECT"}, logical = Logical.OR)
    public R searchAllDept() {
        List<TbDept> list = deptService.searchAllDept();
        return R.ok().put("result", list);
    }

    @PostMapping("/insertDept")
    @ApiOperation("添加部门")
    @RequiresPermissions(value = {"ROOT", "DEPT:INSERT"}, logical = Logical.OR)
    public R insertDept(@Valid @RequestBody InsertDeptForm form) {
        TbDept entity = new TbDept();
        entity.setDeptName(form.getDeptName());
        deptService.insertDept(entity);
        return R.ok().put("result", "success");
    }

    @PostMapping("/updateDept")
    @ApiOperation("修改部门")
    @RequiresPermissions(value = {"ROOT", "DEPT:UPDATE"}, logical = Logical.OR)
    public R updateDept(@Valid @RequestBody UpdateDeptForm form) {
        TbDept entity = new TbDept();
        entity.setId(form.getId());
        entity.setDeptName(form.getDeptName());
        deptService.updateDept(entity);
        return R.ok().put("result", "success");
    }

    @PostMapping("/deleteDeptById")
    @ApiOperation("删除部门")
    @RequiresPermissions(value = {"ROOT", "DEPT:DELETE"}, logical = Logical.OR)
    public R deleteDeptById(@Valid @RequestBody DeleteDeptByIdForm form) {
        deptService.deleteDeptById(form.getId());
        return R.ok().put("result", "success");
    }
}