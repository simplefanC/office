package com.simplefanc.office.checkin.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * sys_config
 * @author 
 */
@Data
public class SysConfig implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 参数名
     */
    private String paramKey;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;
}