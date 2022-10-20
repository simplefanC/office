package com.simplefanc.office.checkin.controller;

import com.simplefanc.office.checkin.controller.form.config.AddOrDeleteWorkdayForm;
import com.simplefanc.office.checkin.controller.form.config.SaveSystemConfigForm;
import com.simplefanc.office.checkin.controller.form.config.SearchScheduleInMonthForm;
import com.simplefanc.office.checkin.service.SysConfigService;
import com.simplefanc.office.checkin.entity.SysConfig;
import com.simplefanc.office.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@Api
@RequestMapping("/sys")
@RestController
public class SysConfigController {
    @Autowired
    private SysConfigService sysConfigService;

    @PostMapping("/getSystemConfig")
    public R getSystemConfig() {
        return R.ok().put("result", sysConfigService.getSystemConfig());
    }

    @PostMapping("/saveSystemConfig")
    public R saveSystemConfig(@RequestBody SaveSystemConfigForm form) {
        List<SysConfig> sysConfigs = form.getSysConfigs();
        sysConfigService.updateBatchById(sysConfigs);
        sysConfigService.applySysConfigsToConstants(sysConfigs);
        return R.ok();
    }

    @PostMapping("/addOrDeleteWorkday")
    @ApiOperation("插入或删除workday")
    public R addOrDeleteWorkday(@Valid @RequestBody AddOrDeleteWorkdayForm form) {
        if ("add".equals(form.getType())) {
            sysConfigService.addWorkday(form.getDate());
        } else if ("delete".equals(form.getType())) {
            sysConfigService.deleteWorkday(form.getDate());
        }
        return R.ok();
    }

    @PostMapping("/addOrDeleteHoliday")
    @ApiOperation("插入或删除holiday")
    public R addOrDeleteHoliday(@Valid @RequestBody AddOrDeleteWorkdayForm form) {
        if ("add".equals(form.getType())) {
            sysConfigService.addHoliday(form.getDate());
        } else if ("delete".equals(form.getType())) {
            sysConfigService.deleteHoliday(form.getDate());
        }
        return R.ok();
    }

    @PostMapping("/searchScheduleInMonth")
    @ApiOperation("查询指定月份的holidays和workdays")
    public R searchScheduleInMonth(@Valid @RequestBody SearchScheduleInMonthForm form) {
        List<String> holidays = sysConfigService.searchHolidaysInMonth(form.getYear() + "/" + form.getMonth());
        List<String> workdays = sysConfigService.searchWorkdaysInMonth(form.getYear() + "/" + form.getMonth());
        return R.ok().put("holidays", holidays).put("workdays", workdays);
    }
}
