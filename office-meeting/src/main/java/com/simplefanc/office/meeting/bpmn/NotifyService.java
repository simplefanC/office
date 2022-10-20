package com.simplefanc.office.meeting.bpmn;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.simplefanc.office.meeting.config.quartz.MeetingRoomJob;
import com.simplefanc.office.meeting.config.quartz.MeetingStatusJob;
import com.simplefanc.office.meeting.config.quartz.QuartzUtil;
import com.simplefanc.office.meeting.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class NotifyService implements JavaDelegate {
    @Autowired
    private MeetingService meetingService;
    @Autowired
    private QuartzUtil quartzUtil;

    /**
     * 审批通过 status 1->3
     * 不通过 1->2
     * 启动成功 1待审批
     * result == 不同意
     * 2审批不通过
     * 3未开始
     * 4进行中
     * 5已结束
     *
     * @param execution
     */
    @Override
    public void execute(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        String uuid = (String) variables.get("uuid");
        String result = (String) variables.get("result");

        HashMap map = this.meetingService.searchMeetingByUUID(uuid);
        DateTime startTime = DateUtil.parse(map.get("date") + " " + map.get("start"));
        DateTime endTime = DateUtil.parse(map.get("date") + " " + map.get("end"));
        if ("不同意".equals(result)) {
            HashMap<String, Object> param = new HashMap<>();
            param.put("uuid", uuid);
            param.put("status", 2);//2审批不通过
            this.meetingService.updateMeetingStatus(param);
        } else {
            HashMap<String, Object> param = new HashMap<>();
            param.put("uuid", uuid);
            param.put("status", 3);//未开始
            this.meetingService.updateMeetingStatus(param);
            //MeetingRoomJob 会议开始前15min创建roomID
            JobDetail jobDetail1 = JobBuilder
                    .newJob(MeetingRoomJob.class)
                    .build();
            jobDetail1.getJobDataMap().put("uuid", uuid);
            jobDetail1.getJobDataMap().put("expireTime", endTime);
            this.quartzUtil.addJob(jobDetail1, uuid, "room:create", DateUtil.offsetMinute(startTime, -15));

            //MeetingStatusJob 进行中
            JobDetail jobDetail2 = JobBuilder
                    .newJob(MeetingStatusJob.class)
                    .build();
            jobDetail2.getJobDataMap().put("uuid", uuid);
            jobDetail2.getJobDataMap().put("status", 4);//进行中
            this.quartzUtil.addJob(jobDetail2, uuid, "status:ing", startTime);
            //MeetingStatusJob 已结束
            JobDetail jobDetail3 = JobBuilder
                    .newJob(MeetingStatusJob.class)
                    .build();
            jobDetail3.getJobDataMap().put("uuid", uuid);
            jobDetail3.getJobDataMap().put("status", 5);//已结束
            this.quartzUtil.addJob(jobDetail3, uuid, "status:end", endTime);
        }
    }
}
