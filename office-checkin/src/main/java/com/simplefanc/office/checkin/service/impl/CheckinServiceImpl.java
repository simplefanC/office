package com.simplefanc.office.checkin.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.simplefanc.office.checkin.dao.*;
import com.simplefanc.office.checkin.service.CheckinService;
import com.simplefanc.office.checkin.task.EmailTask;
import com.simplefanc.office.common.config.SystemConstants;
import com.simplefanc.office.checkin.entity.TbCheckin;
import com.simplefanc.office.checkin.entity.TbFaceModel;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.common.feign.FaceRecognitionFeignClient;
import com.simplefanc.office.common.feign.ServiceUserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author chenfan
 */
@Service
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private SystemConstants constants;

    @Autowired
    private TbHolidaysDao holidaysDao;

    @Autowired
    private TbWorkdayDao workdayDao;

    @Autowired
    private TbCheckinDao checkinDao;

    @Autowired
    private TbFaceModelDao faceModelDao;

    @Autowired
    private TbCityDao cityDao;

//    @Autowired
//    private TbUserDao userDao;
    @Autowired
    private ServiceUserFeignClient userFeignClient;

//    @Autowired
//    private RestTemplate restTemplate;

    @Value("${emos.email.hr}")
    private String hrEmail;

    @Autowired
    private EmailTask emailTask;

    @Autowired
    private FaceRecognitionFeignClient faceRecognitionFeignClient;


    @Override
    public String validCanCheckIn(int userId, String date) {
        String type = "工作日";
        if (DateUtil.date().isWeekend()) {
            type = "节假日";
        }

        // 修正
        if (holidaysDao.searchTodayIsHolidays() != null) {
            type = "节假日";
        } else if (workdayDao.searchTodayIsWorkday() != null) {
            type = "工作日";
        }

        if ("节假日".equals(type)) {
            return "节假日不需要考勤";
        } else {
            DateTime now = DateUtil.date();
            // 考勤开始和结束时间
            String start = DateUtil.today() + " " + constants.attendanceStartTime;
            String end = DateUtil.today() + " " + constants.attendanceEndTime;
            DateTime attendanceStart = DateUtil.parse(start);
            DateTime attendanceEnd = DateUtil.parse(end);
            if (now.isBefore(attendanceStart)) {
                return "没到上班考勤开始时间";
            } else if (now.isAfter(attendanceEnd)) {
                return "超过了上班考勤结束时间";
            } else {
                HashMap<String, Object> map = new HashMap<>(4);
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);
                return checkinDao.haveCheckin(map) != null ? "今日已经考勤，不用重复考勤" : "可以考勤";
            }
        }
    }

    @Override
    public String checkin(HashMap<String, Object> param) {
        Date d1 = DateUtil.date();
        Date d2 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceTime);
        Date d3 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceEndTime);
        int status = 0;
        if (d1.compareTo(d2) <= 0) {
            // 正常考勤
            status = 1;
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            // 迟到
            status = 2;
        }
        int userId = (Integer) param.get("userId");
        String faceModel = faceModelDao.searchFaceModel(userId);
        if (faceModel == null) {
            return "不存在人脸模型";
//            throw new EmosException("不存在人脸模型");
        } else {
            // 图片路径
            String path = (String) param.get("path");
//            HttpRequest request = HttpUtil.createPost(checkinUrl);
//            // 设置请求参数
//            // 文件: photo；模型: targetModel
//            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
//            HttpResponse response = request.execute();
//            if (response.getStatus() != 200) {
//                log.error("人脸识别服务异常");
//                throw new EmosException("人脸识别服务异常");
//            }
//            // 响应内容
//            String body = response.body();
            String body = faceRecognitionFeignClient.checkin(path, faceModel);
//            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
//            paramMap.add("path", path);
//            paramMap.add("targetModel", faceModel);
//            String body = restTemplate.postForObject("http://face-recognition/check_in", paramMap, String.class);
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
//                throw new EmosException(body);
                return body;
            } else if ("False".equals(body)) {
//                throw new EmosException("签到无效，非本人签到");
                return "签到无效，非本人签到";
            } else if ("True".equals(body)) {
                // 查询疫情风险等级
                int risk = 1;
                String city = (String) param.get("city");
                String district = (String) param.get("district");
                String address = (String) param.get("address");
                String country = (String) param.get("country");
                String province = (String) param.get("province");
                if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {
                    String code = cityDao.searchCode(city);
                    try {
                        String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                        Document document = Jsoup.connect(url).get();
                        Elements elements = document.getElementsByClass("list-content");
                        if (elements.size() > 0) {
                            Element element = elements.get(0);
                            String result = element.select("p:last-child").text();
//                            result = "高风险";//测试
                            if ("高风险".equals(result)) {
                                risk = 3;
                                //发送告警邮件
//                                HashMap<String, Object> map1 = userFeignClient.searchNameAndDept(userId);
//                                HashMap<String, String> map = (HashMap<String, String>) map1.get("result");
                                HashMap<String, String> map = userFeignClient.searchNameAndDept(userId);
                                String name = map.get("name");
                                String deptName = map.get("dept_name");
                                deptName = deptName != null ? deptName : "";
                                SimpleMailMessage message = new SimpleMailMessage();
                                message.setTo(hrEmail);
                                message.setSubject("员工" + name + "身处高风险疫情地区警告");
                                message.setText(deptName + "员工" + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" + address + ",属于新冠疫情高风险地区，请及时与该员工联系，核实情况！");
                                //异步发送邮件
                                emailTask.sendAsync(message);
                            } else if ("中风险".equals(result)) {
                                risk = 2;
                            }
                        }
                    } catch (Exception e) {
                        log.error("执行异常", e);
//                        throw new EmosException("获取风险等级失败");
                        return "获取风险等级失败";
                    }
                }
                // 保存签到记录
                TbCheckin entity = new TbCheckin();
                entity.setUserId(userId);
                entity.setAddress(address);
                entity.setCountry(country);
                entity.setProvince(province);
                entity.setCity(city);
                entity.setRisk(risk);
                entity.setDistrict(district);
                entity.setStatus((byte) status);
                entity.setDate(DateUtil.today());
                entity.setCreateTime(d1);
                checkinDao.insert(entity);
            }
            return "签到成功";
        }
    }

    @Override
    public String createFaceModel(int userId, String path) {
//        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
//        request.form("photo", FileUtil.file(path));
//        HttpResponse response = request.execute();
//        String body = response.body();
//        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
//        paramMap.add("path", path);
//        String body = restTemplate.postForObject("http://face-recognition/create_face_model", paramMap, String.class);
        String body = faceRecognitionFeignClient.createFaceModel(path);
        if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
            return body;
        } else {
            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);
            faceModelDao.insert(entity);
            return "人脸建模成功";
        }
    }

    @Override
    public HashMap searchTodayCheckin(int userId) {
        return checkinDao.searchTodayCheckin(userId);
    }

    @Override
    public long searchCheckinDays(int userId) {
        return checkinDao.searchCheckinDays(userId);
    }

    @Override
    public ArrayList<HashMap> searchWeekCheckin(HashMap param) {
        ArrayList<HashMap> checkinList = checkinDao.searchWeekCheckin(param);
        ArrayList<String> holidaysList = holidaysDao.searchHolidaysInRange(param);
        ArrayList<String> workdayList = workdayDao.searchWorkdayInRange(param);
        DateTime startDate = DateUtil.parseDate(param.get("startDate").toString());
        DateTime endDate = DateUtil.parseDate(param.get("endDate").toString());
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);
        ArrayList list = new ArrayList();
        range.forEach(one -> {
            String date = one.toString("yyyy-MM-dd");
            //查看今天是不是假期或者工作日
            String type = "工作日";
            if (one.isWeekend()) {
                type = "节假日";
            }
            if (holidaysList != null && holidaysList.contains(date)) {
                type = "节假日";
            } else if (workdayList != null && workdayList.contains(date)) {
                type = "工作日";
            }
            String status = "";
            if ("工作日".equals(type) && DateUtil.compare(one, DateUtil.date()) <= 0) {
                status = "缺勤";
                boolean flag = false;
                for (HashMap<String, String> map : checkinList) {
                    if (map.containsValue(date)) {
                        status = map.get("status");
                        flag = true;
                        break;
                    }
                    DateTime endTime = DateUtil.parse(DateUtil.today() + " " + constants.attendanceEndTime);
                    String today = DateUtil.today();
                    if (date.equals(today) && DateUtil.date().isBefore(endTime) && !flag) {
                        status = "";
                    }
                }
            }
            HashMap map = new HashMap(4);
            map.put("date", date);
            map.put("status", status);
            map.put("type", type);
            map.put("day", one.dayOfWeekEnum().toChinese("周"));
            list.add(map);
        });
        return list;
    }

    @Override
    public ArrayList<HashMap> searchMonthCheckin(HashMap param) {
        return this.searchWeekCheckin(param);
    }

}
