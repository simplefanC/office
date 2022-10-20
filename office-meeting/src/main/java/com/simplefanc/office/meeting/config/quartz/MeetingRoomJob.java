package com.simplefanc.office.meeting.config.quartz;

import cn.hutool.core.util.RandomUtil;
import com.simplefanc.office.common.service.RedisService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MeetingRoomJob extends QuartzJobBean {
    @Autowired
    private RedisService redisService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String uuid = (String) jobDataMap.get("uuid");
        Date expireTime = (Date) jobDataMap.get("expireTime");
        int randomInt;
        do {
            randomInt = RandomUtil.randomInt(100000000, 1000000000);
        } while (this.redisService.hasKey(String.valueOf(randomInt)));
        //直到会议结束过期
        redisService.set("emos:meeting:no:" + randomInt, uuid, expireTime);
        redisService.set("emos:meeting:uuid:" + uuid, randomInt, expireTime);
    }
}
