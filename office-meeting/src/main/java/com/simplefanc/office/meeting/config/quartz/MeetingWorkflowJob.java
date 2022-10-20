package com.simplefanc.office.meeting.config.quartz;

import com.simplefanc.office.meeting.service.MeetingService;
import com.simplefanc.office.meeting.service.WorkflowService;
import org.activiti.engine.RuntimeService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MeetingWorkflowJob extends QuartzJobBean {
    @Autowired
    private MeetingService meetingService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private WorkflowService workflowService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String processInstanceId = (String) jobDataMap.get("processInstanceId");
        String uuid = (String) jobDataMap.get("uuid");
        if (this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() != null) {
            this.workflowService.deleteProcessById(uuid, processInstanceId, "删除原因");
            HashMap<String, Object> param = new HashMap<>();
            //已结束
            param.put("status", 5);
            param.put("uuid", uuid);
            this.meetingService.updateMeetingStatus(param);
        }
    }
}
