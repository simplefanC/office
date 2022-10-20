package com.simplefanc.office.meeting.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.simplefanc.office.common.feign.ServiceUserFeignClient;
import com.simplefanc.office.meeting.entity.TbMeeting;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.common.service.RedisService;
import com.simplefanc.office.meeting.dao.TbMeetingDao;
import com.simplefanc.office.meeting.service.MeetingService;
import com.simplefanc.office.meeting.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenfan
 */
@Slf4j
@Service
public class MeetingServiceImpl implements MeetingService {
    @Autowired
    private TbMeetingDao meetingDao;
    @Autowired
    private ServiceUserFeignClient userFeignClient;

    //    @Value("${workflow.url}")
//    private String workflow;
//    @Value("${emos.recieveNotify}")
//    private String recieveNotify;
    @Autowired
    private RedisService redisService;
    @Autowired
    private WorkflowService workflowService;


    @Override
    public void insertMeeting(TbMeeting entity) {
        //保存数据
        int row = meetingDao.insertMeeting(entity);
        if (row != 1) {
            throw new EmosException("会议添加失败");
        }
        //开启审批工作流
        startMeetingWorkflow(entity.getUuid(), entity.getCreatorId().intValue(), entity.getDate(), entity.getStart(), entity.getEnd());
    }

    @Override
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param) {
        ArrayList<HashMap> list = meetingDao.searchMyMeetingListByPage(param);
        //按照日期对结果分组
        ArrayList resultList = new ArrayList();
        //会议小列表 日期:JSONArray
        HashMap<String, Object> resultMap = null;
        String date = null;
        JSONArray array = null;
        for (HashMap map : list) {
            String temp = map.get("date").toString();
            //和前一个会议记录日期比较
            if (!temp.equals(date)) {
                date = temp;
                resultMap = new HashMap<>(2);
                resultMap.put("date", date);

                array = new JSONArray();
                resultMap.put("list", array);

                resultList.add(resultMap);
            }
            array.put(map);
        }
        return resultList;
    }

    private void startMeetingWorkflow(String uuid, int creatorId, String date, String start, String end) {
        //查询创建者用户信息
//        HashMap map2 = userFeignClient.searchUserInfo(creatorId);
        HashMap info = userFeignClient.searchUserInfo(creatorId);
        HashMap param = new HashMap();
//        JSONObject json = new JSONObject();
//        param.put("url", recieveNotify);
        param.put("uuid", uuid);
        param.put("creatorId", creatorId);
        param.put("date", date);
        param.put("start", start);
        param.put("end", end);
        String[] roles = info.get("roles").toString().split("，");
        //如果不是总经理创建的会议
        if (!ArrayUtil.contains(roles, "总经理")) {
            //查询总经理ID和同部门的经理的ID
//            HashMap<String, Object> map = userFeignClient.searchDeptManagerId(creatorId);
//            Integer managerId = (Integer) map.get("result");
            Integer managerId = userFeignClient.searchDeptManagerId(creatorId);
            //部门经理ID
            param.put("managerId", managerId);
            //总经理ID
//            HashMap<String, Object> map1 = userFeignClient.searchGmId();
//            Integer gmId = (Integer) map1.get("result");
            Integer gmId = userFeignClient.searchGmId();
            param.put("gmId", gmId);
            //查询会议员工是不是同一个部门
            boolean bool = meetingDao.searchMeetingMembersInSameDept(uuid);
            param.put("sameDept", bool);
        }
        String instanceId = workflowService.startMeetingProcess(param);
        HashMap param2 = new HashMap(2);
        param2.put("uuid", uuid);
        param2.put("instanceId", instanceId);
        //在会议记录中保存工作流实例的ID
        int row = meetingDao.updateMeetingInstanceId(param2);
        if (row != 1) {
            throw new EmosException("保存会议工作流实例ID失败");
        }

//        String url = workflow + "/workflow/startMeetingProcess";
//        //请求工作流接口，开启工作流
//        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json").body(json.toString()).execute();
//        if (response.getStatus() == 200) {
//            json = JSONUtil.parseObj(response.body());
//            //如果工作流创建成功，就更新会议状态
//            String instanceId = json.getStr("instanceId");
//            HashMap param = new HashMap(2);
//            param.put("uuid", uuid);
//            param.put("instanceId", instanceId);
//            //在会议记录中保存工作流实例的ID
//            int row = meetingDao.updateMeetingInstanceId(param);
//            if (row != 1) {
//                throw new EmosException("保存会议工作流实例ID失败");
//            }
//        }
    }

    @Override
    public HashMap searchMeetingById(int id) {
        HashMap map = meetingDao.searchMeetingById(id);
        ArrayList<HashMap> list = meetingDao.searchMeetingMembers(id);
        map.put("members", list);
        return map;
    }

    @Override
    public void updateMeetingInfo(HashMap param) {
        int id = (int) param.get("id");
        String date = param.get("date").toString();
        String start = param.get("start").toString();
        String end = param.get("end").toString();
        String instanceId = param.get("instanceId").toString();
        //查询修改前的会议记录
        HashMap oldMeeting = meetingDao.searchMeetingById(id);
        String uuid = oldMeeting.get("uuid").toString();
        Integer creatorId = Integer.parseInt(oldMeeting.get("creatorId").toString());
        //更新会议记录
        int row = meetingDao.updateMeetingInfo(param);
        if (row != 1) {
            throw new EmosException("会议更新失败");
        }
        //会议更新成功之后，删除以前的工作流
//        JSONObject json = new JSONObject();
//        param.put("instanceId", instanceId);
//        param.put("reason", "会议被修改");
//        param.put("uuid", uuid);
        this.workflowService.deleteProcessById(uuid, instanceId, "会议被修改");
//        String url = workflow + "/workflow/deleteProcessById";
//        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(json.toString()).execute();
//        if (resp.getStatus() != 200) {
//            log.error("删除工作流失败");
//            throw new EmosException("删除工作流失败");
//        }
        //创建新的工作流
        startMeetingWorkflow(uuid, creatorId, date, start, end);
    }

    @Override
    public void deleteMeetingById(int id) {
        //查询会议信息
        HashMap meeting = meetingDao.searchMeetingById(id);
        String uuid = meeting.get("uuid").toString();
        String instanceId = meeting.get("instanceId").toString();
        DateTime date = DateUtil.parse(meeting.get("date") + " " + meeting.get("start"));
        DateTime now = DateUtil.date();
        //会议开始前20分钟，不能删除会议
        if (now.isAfterOrEquals(date.offset(DateField.MINUTE, -20))) {
            throw new EmosException("距离会议开始不足20分钟，不能删除会议");
        }
        int row = meetingDao.deleteMeetingById(id);
        if (row != 1) {
            throw new EmosException("会议删除失败");
        }
        //删除会议工作流
        this.workflowService.deleteProcessById(uuid, instanceId, "会议被取消");
//        JSONObject json = new JSONObject();
//        json.set("instanceId", instanceId);
//        json.set("reason", "会议被取消");
//        json.set("uuid", uuid);
//        String url = workflow + "/workflow/deleteProcessById";
//        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(json.toString()).execute();
//        if (resp.getStatus() != 200) {
//            log.error("删除工作流失败");
//            throw new EmosException("删除工作流失败");
//        }
    }

    @Override
    public Integer searchRoomIdByUUID(String uuid) {
        return Integer.parseInt(redisService.get("emos:meeting:uuid:" + uuid).toString());
    }

    @Override
    public List<String> searchUserMeetingInMonth(HashMap map) {
        return this.meetingDao.searchUserMeetingInMonth(map);
    }

    @Override
    public HashMap searchMeetingByInstanceId(String instanceId) {
        HashMap map = meetingDao.searchMeetingByInstanceId(instanceId);
        boolean inSameDept = meetingDao.searchMeetingMembersInSameDept((String) map.get("uuid"));
        map.put("sameDept", inSameDept);
        long hours = DateUtil.between((Date) map.get("start"), (Date) map.get("end"), DateUnit.HOUR);
        map.put("hours", hours);
        return map;
    }

    @Override
    public HashMap searchMeetingByUUID(String uuid) {
        return this.meetingDao.searchMeetingByUUID(uuid);
    }

    @Override
    public void updateMeetingStatus(HashMap map) {
        if (this.meetingDao.updateMeetingStatus(map) != 1) {
            throw new EmosException("会议状态更新失败");
        }
    }
}