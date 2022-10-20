package com.simplefanc.office.meeting.service;

import java.util.ArrayList;
import java.util.HashMap;

public interface WorkflowService {
    void approvalMeeting(HashMap param);

    boolean searchProcessStatus(String p0);

    ArrayList searchUserTaskListByPage(HashMap param);

    void deleteProcessById(String uuid, String instanceId, String reason);

    ArrayList searchProcessUsers(String p0);

    String startMeetingProcess(HashMap param);
}
