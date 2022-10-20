package com.simplefanc.office.meeting.controller;

import com.simplefanc.office.common.util.JwtUtil;
import com.simplefanc.office.common.util.R;
import com.simplefanc.office.meeting.controller.form.workflow.ApprovalMeetingForm;
import com.simplefanc.office.meeting.controller.form.workflow.DeleteProcessByIdForm;
import com.simplefanc.office.meeting.controller.form.workflow.SearchUserTaskListByPageForm;
import com.simplefanc.office.meeting.controller.form.workflow.StartMeetingProcessForm;
import com.simplefanc.office.meeting.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping({"/workflow"})
public class WorkFlowController {
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping({"/approvalMeeting"})
    public R approvalMeeting(@Valid @RequestBody ApprovalMeetingForm form) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("taskId", form.getTaskId());
        //"同意" "不同意"
        hashMap.put("approval", form.getApproval());
        this.workflowService.approvalMeeting(hashMap);
        return R.ok();
    }

    @PostMapping({"/searchUserTaskListByPage"})
    public R searchUserTaskListByPage(@Valid @RequestBody SearchUserTaskListByPageForm form, @RequestHeader("token") String token) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int userId = this.jwtUtil.getUserId(token);
        hashMap.put("userId", userId);
        hashMap.put("page", form.getPage());
        hashMap.put("length", form.getLength());
        hashMap.put("type", form.getType());
        ArrayList arrayList = this.workflowService.searchUserTaskListByPage(hashMap);
        return R.ok().put("result", arrayList);
    }


    @PostMapping({"/startMeetingProcess"})
    public R startMeetingProcess(@Valid @RequestBody StartMeetingProcessForm form) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("creatorId", form.getOpenId());
        hashMap.put("url", form.getUrl());
        hashMap.put("sameDept", form.getSameDept());
        hashMap.put("uuid", form.getUuid());
        hashMap.put("managerId", form.getManagerId());
        hashMap.put("gmId", form.getGmId());
        hashMap.put("date", form.getDate());
        hashMap.put("start", form.getStart());
        hashMap.put("end", form.getEnd());
        String instanceId = this.workflowService.startMeetingProcess(hashMap);
        return R.ok().put("instanceId", instanceId);
    }

    @PostMapping({"/deleteProcessById"})
    public R deleteProcessById(@Valid @RequestBody DeleteProcessByIdForm form) {
        this.workflowService.deleteProcessById(form.getUuid(), form.getInstanceId(), form.getReason());
        return R.ok();
    }

//    @PostMapping({"/searchProcessUsers"})
//    public R searchProcessUsers(@Valid @RequestBody SearchProcessUsersForm a) {
//         String allatorIxDEMO;
//        if ((allatorIxDEMO = this.ALLATORIxDEMO(a.getCode())) != null) {
//            return R.error(allatorIxDEMO);
//        }
//        a = (SearchProcessUsersForm) this.workflowService.searchProcessUsers(a.getInstanceId());
//        return R.ok().put(DeleteProcessByIdForm.ALLATORIxDEMO("($)465"), a);
//    }
//
//
//    private String ALLATORIxDEMO(String a) {
//        if (((JSONObject) (a = (String) JSONUtil.parseObj(((HttpRequest) HttpRequest.get(new StringBuilder().insert(0, ApprovalMeetingForm.ALLATORIxDEMO("lMpIw\u0003+\u0016eImJ*PiVkZ*ZkT;PgV`\\9")).append(a).toString()).header(DeleteProcessByIdForm.ALLATORIxDEMO("\u0019.45?/.l\u000e8*$"), ApprovalMeetingForm.ALLATORIxDEMO("XtIhPgXpPkW+SwVj"))).execute().body()))).getInt((Object) DeleteProcessByIdForm.ALLATORIxDEMO("9.>$")) == 1000) {
//            return null;
//        }
//        return ((JSONObject) a).getStr((Object) ApprovalMeetingForm.ALLATORIxDEMO("iJc"));
//    }
//
//
//    @PostMapping({"/searchProcessStatus"})
//    public R searchProcessStatus(@Valid @RequestBody  SearchProcessStatusForm a) {
//         String allatorIxDEMO;
//        if ((allatorIxDEMO = this.ALLATORIxDEMO(a.getCode())) != null) {
//            return R.error(allatorIxDEMO);
//        }
//        if (!this.workflowService.searchProcessStatus(a.getInstanceId())) {
//            return R.ok().put(DeleteProcessByIdForm.ALLATORIxDEMO("($)465"), ApprovalMeetingForm.ALLATORIxDEMO("\u672e\u7eea\u675b"));
//        }
//        return R.ok().put(DeleteProcessByIdForm.ALLATORIxDEMO("($)465"), ApprovalMeetingForm.ALLATORIxDEMO("\u5df6\u7eea\u675b"));
//    }
}
