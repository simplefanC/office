package com.simplefanc.office.meeting.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.simplefanc.office.common.dto.MessageEntity;
import com.simplefanc.office.common.feign.ServiceMessageFeignClient;
import com.simplefanc.office.meeting.config.quartz.MeetingWorkflowJob;
import com.simplefanc.office.meeting.config.quartz.QuartzUtil;
import com.simplefanc.office.meeting.service.MeetingService;
import com.simplefanc.office.meeting.service.WorkflowService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkflowServiceImpl implements WorkflowService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private MeetingService meetingService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private QuartzUtil quartzUtil;
    @Autowired
    private ServiceMessageFeignClient messageFeignClient;

    @Override
    public boolean searchProcessStatus(String a) {
        return this.runtimeService.createProcessInstanceQuery()
                .processInstanceId(a)
                .singleResult() == null;
    }

    @Override
    public void approvalMeeting(HashMap param) {
        String taskId = (String) param.get("taskId");
        String result = (String) param.get("approval");
        this.taskService.setVariableLocal(taskId, "result", result);
        this.taskService.complete(taskId);
        //todo 通知下一审批人或流程结束
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        notifyNextAssignee(historicTaskInstance.getProcessInstanceId());
    }

    @Override
    public ArrayList searchProcessUsers(String instanceId) {
        List<HistoricTaskInstance> instanceList = this.historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(instanceId)
                .finished()
                .list();
        ArrayList<String> list = new ArrayList<>();
        instanceList.forEach(instance -> list.add(instance.getAssignee()));
        return list;
    }

    @Override
    public String startMeetingProcess(HashMap param) {
//        如果是总经理创建的会议 managerId为null
        param.put("identity", param.get("managerId") == null ? "总经理" : "");
        String processInstanceId =
                this.runtimeService
                        .startProcessInstanceByKey("meeting", param)
                        .getProcessInstanceId();

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee(param.get("creatorId").toString())
                .list()
                .get(0);

        // 完成任务,并传递流程变量
        taskService.complete(task.getId(), param);
        //todo 通知下一审批人或流程结束
        notifyNextAssignee(processInstanceId);

        JobDetail jobDetail = JobBuilder
                //删除流程实例&更新会议状态为“已结束”
                .newJob(MeetingWorkflowJob.class)
                .build();
        jobDetail.getJobDataMap().put("processInstanceId", processInstanceId);
        String uuid = (String) param.get("uuid");
        jobDetail.getJobDataMap().put("uuid", uuid);
        //不管审批通过与否
        quartzUtil.addJob(jobDetail, uuid, "job:end", DateUtil.parse(param.get("date") + " " + param.get("end")));

        return processInstanceId;
    }

    private void notifyNextAssignee(String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task != null) {
            //发送系统消息
            MessageEntity entity = new MessageEntity();
            entity.setSenderId(0);
            entity.setSenderName("系统消息");
            entity.setUuid(IdUtil.simpleUUID());
            entity.setMsg("您有一个待处理审批，请及时处理，谢谢！");
            entity.setSendTime(new Date());
            messageFeignClient.sendAsync(task.getAssignee() + "", entity);
        }
    }

    @Override
    public void deleteProcessById(String uuid, String instanceId, String reason) {
        if (this.runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).count() > 0L) {
            this.runtimeService.deleteProcessInstance(instanceId, reason);
        }
        if (this.historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId).count() > 0L) {
            this.historyService.deleteHistoricProcessInstance(instanceId);
        }
        //startMeetingProcess时
        this.quartzUtil.deleteJob(uuid, "job:end");
        //NotifyService中审批通过时add的更新状态的任务
        this.quartzUtil.deleteJob(uuid, "room:create");
        this.quartzUtil.deleteJob(uuid, "status:ing");
        this.quartzUtil.deleteJob(uuid, "status:end");
    }

    @Override
    public ArrayList searchUserTaskListByPage(HashMap param) {
        ArrayList list = new ArrayList();
        //指定个人任务查询 未审批
        //已审批 可以从历史表查询到
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        String userId = String.valueOf(param.get("userId"));
        int start = (page - 1) * length;
        if ("待审批".equals(param.get("type"))) {
            List<Task> taskList = taskService.createTaskQuery()
                    .taskAssignee(userId)
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(start, length);
            taskList.forEach(task -> {
                String instanceId = task.getProcessInstanceId();
                HashMap hashMap = meetingService.searchMeetingByInstanceId(instanceId);
                hashMap.put("taskId", task.getId());
                hashMap.put("processStatus", "未结束");
                hashMap.put("processType", "meeting");
                list.add(hashMap);
            });
        } else if ("已审批".equals(param.get("type"))) {
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .includeTaskLocalVariables()
                    .includeProcessVariables()
                    .taskAssignee(userId)
                    .finished()
                    .orderByTaskCreateTime()
                    .desc()
                    .list();
            historicTaskInstances.forEach(historicTaskInstance -> {
                if (!"创建会议".equals(historicTaskInstance.getName())) {
                    String instanceId = historicTaskInstance.getProcessInstanceId();
                    Map<String, Object> variables = historicTaskInstance.getTaskLocalVariables();
                    HashMap hashMap = meetingService.searchMeetingByInstanceId(instanceId);
                    hashMap.put("processType", "meeting");
                    hashMap.put("result_1", variables.get("result"));
                    //本人审批
                    if (runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult() != null) {
                        hashMap.put("processStatus", "未结束");
                    } else {
                        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery()
                                .includeTaskLocalVariables()
                                .includeProcessVariables()
                                .processInstanceId(instanceId)
                                .orderByHistoricTaskInstanceEndTime()
                                .orderByTaskCreateTime()
                                .desc()
                                .list()
                                .get(0);
                        Map<String, Object> localVariables = taskInstance.getTaskLocalVariables();
                        hashMap.put("processStatus", "已结束");
                        hashMap.put("result_2", localVariables.get("result"));
                        hashMap.put("lastUser", taskInstance.getAssignee());
                    }
                    list.add(hashMap);
                }
            });
        }
        return list;
    }
}
