package com.simplefanc.office.user.controller.form.user;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateUserInfoForm {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 身份证号
     */
    private String idNum;
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像网址
     */
    private String photo;

    /**
     * 性别
     */
    private Object sex;

    /**
     * 手机号码
     */
    private String tel;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 入职日期
     */
    private Date hiredate;

    /**
     * 角色 JSON字符串
     */
    private Object role;

    /**
     * 部门编号
     */
    private Integer deptId;

    /**
     * 状态
     */
    private Byte status;
}
