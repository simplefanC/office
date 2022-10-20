package com.simplefanc.office.checkin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.simplefanc.office.checkin.entity.SysConfig;

import java.util.List;

public interface SysConfigService extends IService<SysConfig> {
    List<SysConfig> getSystemConfig();

    void addWorkday(String date);

    void deleteWorkday(String date);

    void addHoliday(String date);

    void deleteHoliday(String date);

    List<String> searchHolidaysInMonth(String yearMonth);

    List<String> searchWorkdaysInMonth(String yearMonth);

    void applySysConfigsToConstants(List<SysConfig> list);
}
