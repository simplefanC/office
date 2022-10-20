package com.simplefanc.office.meeting.config.quartz;

import com.simplefanc.office.meeting.service.MeetingService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.HashMap;

public class MeetingStatusJob extends QuartzJobBean {
    @Autowired
    private MeetingService meetingService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        HashMap<String, Object> param = new HashMap<>();
        param.put("uuid", jobDataMap.get("uuid"));
        param.put("status", jobDataMap.get("status"));
        this.meetingService.updateMeetingStatus(param);
    }
}
