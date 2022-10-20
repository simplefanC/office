package com.simplefanc.office.checkin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simplefanc.office.checkin.dao.SysConfigDao;
import com.simplefanc.office.checkin.dao.TbHolidaysDao;
import com.simplefanc.office.checkin.dao.TbWorkdayDao;
import com.simplefanc.office.checkin.service.SysConfigService;
import com.simplefanc.office.common.config.SystemConstants;
import com.simplefanc.office.checkin.entity.SysConfig;
import com.simplefanc.office.checkin.entity.TbHolidays;
import com.simplefanc.office.checkin.entity.TbWorkday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfig> implements SysConfigService {

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private TbWorkdayDao workdayDao;

    @Autowired
    private TbHolidaysDao holidaysDao;
    @Autowired
    private SystemConstants constants;

    @Override
    public List<SysConfig> getSystemConfig() {
        return sysConfigDao.selectList(new QueryWrapper<SysConfig>().eq("status", "1"));
    }

    @Override
    public void applySysConfigsToConstants(List<SysConfig> list) {
        list.forEach(one -> {
            String key = one.getParamKey();
            // 转成驼峰命名
            key = StrUtil.toCamelCase(key);
            String value = one.getParamValue();
            try {
                Field field = constants.getClass().getDeclaredField(key);
                field.set(constants, value);
            } catch (Exception e) {
                log.error("执行异常", e);
            }
        });
    }

    @Override
    public void addWorkday(String date) {
        workdayDao.insert(TbWorkday.builder().date(DateUtil.parse(date)).build());
    }

    @Override
    public void deleteWorkday(String date) {
        workdayDao.delete(new QueryWrapper<TbWorkday>().eq("date", date));
    }

    @Override
    public void addHoliday(String date) {
        holidaysDao.insert(TbHolidays.builder().date(DateUtil.parse(date)).build());
    }

    @Override
    public void deleteHoliday(String date) {
        holidaysDao.delete(new QueryWrapper<TbHolidays>().eq("date", date));
    }

    @Override
    public List<String> searchHolidaysInMonth(String yearMonth) {
        return holidaysDao.searchHolidaysInMonth(yearMonth);
    }

    @Override
    public List<String> searchWorkdaysInMonth(String yearMonth) {
        return workdayDao.searchWorkdayInMonth(yearMonth);
    }
}
