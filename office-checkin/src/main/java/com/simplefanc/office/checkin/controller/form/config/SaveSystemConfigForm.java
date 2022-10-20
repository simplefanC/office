package com.simplefanc.office.checkin.controller.form.config;

import com.simplefanc.office.checkin.entity.SysConfig;
import lombok.Data;

import java.util.List;

@Data
public class SaveSystemConfigForm {
    /**
     * 注意名称与前端JSON的key相同
     */
    private List<SysConfig> sysConfigs;
}
