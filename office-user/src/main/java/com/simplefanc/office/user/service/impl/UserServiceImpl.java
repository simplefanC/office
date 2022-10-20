package com.simplefanc.office.user.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.simplefanc.office.common.dto.MessageEntity;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.common.dto.UserDTO;
import com.simplefanc.office.common.feign.ServiceMessageFeignClient;
import com.simplefanc.office.user.task.EmailTask;
import com.simplefanc.office.user.dao.TbDeptDao;
import com.simplefanc.office.user.dao.TbUserDao;
import com.simplefanc.office.user.entity.TbUser;
import com.simplefanc.office.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author chenfan
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${emos.wx.app-id}")
    private String appId;

    @Value("${emos.wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private ServiceMessageFeignClient messageFeignClient;

    @Autowired
    private TbDeptDao deptDao;
    @Autowired
    private EmailTask emailTask;

    /**
     * 可定义为私有方法，接口中就不用定义
     */
    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap<String, Object> map = new HashMap<>(4);
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        if (StrUtil.isBlank(openId)) {
            throw new RuntimeException("临时登录凭证错误");
        }
        return openId;
    }

    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        if ("000000".equals(registerCode)) {
            boolean bool = userDao.haveRootUser();
            if (!bool) {
                String openId = getOpenId(code);
//                HashMap<String, Object> param = new HashMap<>(7);
//                param.put("openId", openId);
//                param.put("nickname", nickname);
//                param.put("photo", photo);
//                param.put("role", "[0]");
//                param.put("status", 1);
//                param.put("createTime", new Date());
//                param.put("root", true);
//                userDao.insert(param);

                //发送系统消息
                MessageEntity entity = new MessageEntity();
                entity.setSenderId(0);
                entity.setSenderName("系统消息");
                entity.setUuid(IdUtil.simpleUUID());
                entity.setMsg("欢迎您注册成为超级管理员，请及时更新你的员工个人信息。");
                entity.setSendTime(new Date());
                Integer id = userDao.searchIdByOpenId(openId);
                messageFeignClient.sendAsync(id + "", entity);

                return id;
            } else {
                throw new EmosException("无法绑定超级管理员账号");
            }
        } else {

        }
        return 0;
    }

    @Override
    public Set<String> searchUserPermissions(int userId) {
        return userDao.searchUserPermissions(userId);
    }

    @Override
    public Map<String, Object> login(String code) {
        Map<String, Object> map = new HashMap<>();
        String openId = getOpenId(code);
        //        if (id == null) {
//            throw new EmosException("帐户不存在");
//        }
        //从消息队列中接收消息，转移到消息表
//        messageTask.receiveAysnc(id + "");
        map.put("id", userDao.searchIdByOpenId(openId));
        map.put("openId", openId);
        return map;
    }

    @Override
    public TbUser searchById(int userId) {
        return userDao.searchById(userId);
    }

    @Override
    public String searchUserHiredate(int userId) {
        return userDao.searchUserHiredate(userId);
    }

    @Override
    public HashMap searchUserSummary(int userId) {
        return userDao.searchUserSummary(userId);
    }

    @Override
    public ArrayList<HashMap> searchUserGroupByDept(String keyword, Long deptId, Long roleId) {
        //count deptName id
        ArrayList<HashMap> list1 = deptDao.searchDeptMembers(keyword);
        //uid uname did dname
        ArrayList<HashMap> list2 = userDao.searchUserGroupByDept(keyword);
        for (HashMap map1 : list1) {
            long deptId1 = (Long) map1.get("id");
            if (deptId != null && roleId != null && (roleId == 2 || roleId == 3)) {//todo 硬编码部门经理||普通员工
                if (deptId1 == deptId) {
                    fun(list2, map1, deptId1);
                    ArrayList<HashMap> hashMaps = new ArrayList<>();
                    hashMaps.add(map1);
                    return hashMaps;
                }
            } else {
                fun(list2, map1, deptId1);
            }
        }
        return list1;
    }

    private void fun(ArrayList<HashMap> list2, HashMap map1, long deptId1) {
        ArrayList members = new ArrayList();
        for (HashMap map2 : list2) {
            long id = (Long) map2.get("deptId");
            if (deptId1 == id) {
                members.add(map2);
            }
        }
        map1.put("members", members);
    }

    @Override
    public ArrayList<HashMap> searchMembers(List param) {
        return userDao.searchMembers(param);
    }

    @Override
    public List<HashMap> selectUserPhotoAndName(List param) {
        return userDao.selectUserPhotoAndName(param);
    }

    @Override
    public Integer loginByPass(String id, String password, String openId) {
        TbUser user = userDao.searchById(Integer.parseInt(id));
        if (user == null) {
            throw new EmosException("工号或密码错误");
        }
        //说明未绑定
        if (StrUtil.isBlank(user.getPassword())) {
            //验证密码是否为身份证后6位
            String lastSixIdNum = user.getIdNum().substring(12);
            if (password.equals(lastSixIdNum)) {
                //更新密码和openId
                user.setOpenId(openId);
                user.setPassword(SecureUtil.md5(lastSixIdNum));
                userDao.update(user, new UpdateWrapper<TbUser>().eq("id", id));
            } else {
                throw new EmosException("工号或密码错误");
            }
        }
        if (SecureUtil.md5(password).equals(user.getPassword())) {
            return Integer.parseInt(id);
        }
        throw new EmosException("工号或密码错误");
    }

    @Override
    public String searchMemberEmail(int id) {
        return userDao.searchMemberEmail(id);
    }

    @Override
    public void updateUserPhoto(int userId, String photoUrl) {
        int row = userDao.updateUserPhoto(userId, photoUrl);
        if (row != 1) {
            throw new EmosException("头像更新失败");
        }
    }

    @Override
    public ArrayList<UserDTO> searchAllUser() {
        return userDao.searchAllUser();
    }

    @Override
    public void resetPass(int userId, String oldPass, String newPass) {
        TbUser tbUser = userDao.searchById(userId);
        if (tbUser.getPassword().equals(SecureUtil.md5(oldPass))) {
            tbUser.setPassword(SecureUtil.md5(newPass));
            userDao.update(tbUser, new UpdateWrapper<TbUser>().eq("id", userId));
        } else {
            throw new EmosException("旧密码输入错误");
        }
    }

    @Override
    public void insertOrUpdateUserInfo(TbUser tbUser) {
        //添加员工
        if (tbUser.getId() == 0) {
            tbUser.setId(null);
            tbUser.setCreateTime(new Date());
            tbUser.setRoot(false);
            tbUser.setStatus((byte) 1);
            userDao.insert(tbUser);
            System.out.println(tbUser.getId());
            //发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(tbUser.getEmail());
            message.setSubject("账号开通成功提醒");
            message.setText(tbUser.getName() + "：\n" +
                    "     您好！\n" +
                    "     您的在线办公系统账号已开通成功！\n" +
                    "     请使用工号：" + tbUser.getId() + "（初始密码为您的身份证号后6位）登录系统，并及时修改个人密码和更新您的个人信息，谢谢！");
            //异步发送邮件
            emailTask.sendAsync(message);

            //发送系统消息
            MessageEntity entity = new MessageEntity();
            entity.setSenderId(0);
            entity.setSenderName("系统消息");
            entity.setUuid(IdUtil.simpleUUID());
            entity.setMsg("欢迎您成为本系统用户，请及时更新你的个人信息。");
            entity.setSendTime(new Date());
            messageFeignClient.sendAsync(tbUser.getId() + "", entity);
        } else {
            userDao.update(tbUser, new UpdateWrapper<TbUser>().eq("id", tbUser.getId()));
        }
    }

    @Override
    public void deleteUserById(Integer id) {
        TbUser tbUser = new TbUser();
        tbUser.setStatus((byte) 0);
        userDao.update(tbUser, new UpdateWrapper<TbUser>().eq("id", id));
    }

    @Override
    public HashMap searchUserInfo(int userId) {
        return userDao.searchUserInfo(userId);
    }

    @Override
    public Integer searchDeptManagerId(Integer userId) {
        return userDao.searchDeptManagerId(userId);
    }

    @Override
    public Integer searchGmId() {
        return userDao.searchGmId();
    }

    @Override
    public HashMap<String, String> searchNameAndDept(int userId) {
        return userDao.searchNameAndDept(userId);
    }

    @Override
    public void checkValidRole(Integer id, Integer deptId, Integer roleId) {
        if (roleId == 1) {
            Integer gmId = userDao.searchGmId();
            if (gmId != null && !Objects.equals(gmId, id)) {
                throw new EmosException("已存在总经理");
            }
        } else if (roleId == 2) {
            Integer mangerId = userDao.searchDeptMangerIdByDeptId(deptId);
            if (mangerId != null && !Objects.equals(mangerId, id)) {
                throw new EmosException("该部门已存在部门经理");
            }
        }
    }
}
