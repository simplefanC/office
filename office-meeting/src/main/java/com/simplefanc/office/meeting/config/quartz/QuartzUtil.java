package com.simplefanc.office.meeting.config.quartz;

import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class QuartzUtil {
    @Resource
    private Scheduler scheduler;

    public void deleteJob(String name, String group) {
        try {
            this.scheduler.pauseTrigger(TriggerKey.triggerKey(name, group));
            this.scheduler.unscheduleJob(TriggerKey.triggerKey(name, group));
            this.scheduler.deleteJob(JobKey.jobKey(name, group));
        } catch (SchedulerException e) {
        }
    }

    public void addJob(JobDetail jobDetail, String name, String group, Date date) {
        try {
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(name, group)
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                    )
//                    定义触发器开始时间，如果早于当前时间或没有定义则默认为当前时间
                    .startAt(date)
                    .build();
            this.scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
        }
    }
}
