package com.simplefanc.office.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.simplefanc.office.common.auth.RequiresPermissions;
import com.simplefanc.office.common.auth.Logical;
import com.simplefanc.office.user.task.EmailTask;
import com.simplefanc.office.user.entity.TbUser;
import com.simplefanc.office.common.dto.UserDTO;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.common.service.RedisService;
import com.simplefanc.office.common.util.JwtUtil;
import com.simplefanc.office.common.util.R;
import com.simplefanc.office.user.controller.form.user.*;
import com.simplefanc.office.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author chenfan
 */
@RestController
@RequestMapping("/user")
@Api("用户模块Web接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisService redisService;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;
    @Value("${emos.oss.endpoint}")
    private String endpoint;
    @Value("${emos.oss.access-key-id}")
    private String accessKeyId;
    @Value("${emos.oss.access-key-secret}")
    private String accessKeySecret;
    @Value("${emos.oss.dir-prefix}")
    private String dirPrefix;
    @Value("${emos.oss.bucket-name}")
    private String bucketName;
    @Value("${emos.redis.database}")
    private String REDIS_DATABASE;
    @Value("${emos.redis.key.permission-set}")
    private String REDIS_KEY_PERMISSION_SET;

    @Autowired
    private EmailTask emailTask;

    @GetMapping("/test")
    public R testAsync() throws InterruptedException {
        System.out.println("哈哈哈");
        emailTask.test();
        System.out.println("呵呵呵");
        return R.ok();
    }

    @GetMapping("/searchNameAndDept/{userId}")
    public HashMap<String, String> searchNameAndDept(@PathVariable("userId") int userId) {
        return this.userService.searchNameAndDept(userId);
    }

    @GetMapping("/searchGmId")
    public Integer searchGmId() {
        return this.userService.searchGmId();
    }

    @GetMapping("/searchDeptManagerId/{userId}")
    public Integer searchDeptManagerId(@PathVariable("userId") Integer userId) {
        return this.userService.searchDeptManagerId(userId);
    }

    @GetMapping("/searchUserInfo/{userId}")
    public HashMap searchUserInfo(@PathVariable("userId") Integer userId) {
        return this.userService.searchUserInfo(userId);
    }

    @GetMapping("/searchById/{userId}")
    public TbUser searchById(@PathVariable("userId") Integer userId) {
//        return R.ok().put("result", this.userService.searchById(userId));
        return this.userService.searchById(userId);
    }

    @GetMapping("/searchUserHiredate/{userId}")
    public String searchUserHiredate(@PathVariable("userId") Integer userId) {
//        return R.ok().put("result", this.userService.searchUserHiredate(userId));
        return this.userService.searchUserHiredate(userId);
    }

    @GetMapping("/checkValidRole")
    public R checkValidRole(
            @RequestParam("id") Integer id,
            @RequestParam("deptId") Integer deptId,
            @RequestParam("roleId") Integer roleId) {
        this.userService.checkValidRole(id, deptId, roleId);
        return R.ok();
    }

//    @PostMapping("/register")
//    @ApiOperation("注册用户")
//    public R register(@Valid @RequestBody RegisterForm form) {
//        int id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getNickname(), form.getPhoto());
//        String token = jwtUtil.createToken(id);
//        Set<String> permsSet = userService.searchUserPermissions(id);
//        redisService.set(token, id + "", cacheExpire, TimeUnit.DAYS);
//        return R.ok("用户注册成功").put("token", token).put("permission", permsSet);
//    }

    @PostMapping("/login")
    @ApiOperation("登录系统")
    public R login(@Valid @RequestBody LoginForm form) {
        Map<String, Object> loginRes = userService.login(form.getCode());
        Integer id = (Integer) loginRes.get("id");
        if (id == null) {
            return R.ok("帐户不存在").put("openId", loginRes.get("openId"));
        }
        String token = jwtUtil.createToken(id);
        redisService.set("emos:ums:token:" + token, id + "", cacheExpire, TimeUnit.DAYS);
        Set<String> permsSet = userService.searchUserPermissions(id);
        redisService.set(REDIS_DATABASE + ":" + REDIS_KEY_PERMISSION_SET + ":" + id, permsSet, cacheExpire, TimeUnit.DAYS);
        return R.ok("登录成功").put("token", token).put("permission", permsSet);
    }

    @PostMapping("/loginByPass")
    @ApiOperation("登录系统")
    public R loginByPass(@Valid @RequestBody LoginByPassForm form) {
        int id = userService.loginByPass(form.getId(), form.getPassword(), form.getOpenId());
        String token = jwtUtil.createToken(id);
        redisService.set("emos:ums:token:" + token, id + "", cacheExpire, TimeUnit.DAYS);
        Set<String> permsSet = userService.searchUserPermissions(id);
        redisService.set(REDIS_DATABASE + ":" + REDIS_KEY_PERMISSION_SET + ":" + id, permsSet, cacheExpire, TimeUnit.DAYS);
        return R.ok("登录成功").put("token", token).put("permission", permsSet);
    }

    @PostMapping("/resetPass")
    @ApiOperation("重置密码")
    public R resetPass(@Valid @RequestBody ResetPassForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        userService.resetPass(userId, form.getOldPass(), form.getNewPass());
        return R.ok("重置密码成功");
    }

    @GetMapping("/searchUserSummary")
    @ApiOperation("查询用户摘要信息")
    public R searchUserSummary(
            @RequestHeader("token") String token,
            @RequestParam(value = "userId", required = false) Integer userId) {
        if (userId == null) {
            userId = jwtUtil.getUserId(token);
        }
        HashMap map = userService.searchUserSummary(userId);
        return R.ok().put("result", map);
    }

    @PostMapping("/searchUserGroupByDept")
    @ApiOperation("查询员工列表，按照部门分组排列")
    @RequiresPermissions(value = {"ROOT", "EMPLOYEE:SELECT"}, logical = Logical.OR)
    public R searchUserGroupByDept(@Valid @RequestBody SearchUserGroupByDeptForm form) {
        ArrayList<HashMap> list = userService.searchUserGroupByDept(form.getKeyword(), form.getDeptId(), form.getRoleId());
        return R.ok().put("result", list);
    }

    @PostMapping("/searchAllUser")
    @ApiOperation("查询员工列表")
    public R searchAllUser() {
        ArrayList<UserDTO> list = userService.searchAllUser();
        return R.ok().put("result", list);
    }

    @PostMapping("/searchMembers")
    @ApiOperation("查询成员")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT", "MEETING:UPDATE"}, logical = Logical.OR)
    public R searchMembers(@Valid @RequestBody SearchMembersForm form) {
        if (!JSONUtil.isJsonArray(form.getMembers())) {
            throw new EmosException("members不是JSON数组");
        }
        List param = JSONUtil.parseArray(form.getMembers()).toList(Integer.class);
        ArrayList list = userService.searchMembers(param);
        return R.ok().put("result", list);
    }

    @PostMapping("/selectUserPhotoAndName")
    @ApiOperation("查询用户姓名和头像")
//    @RequiresPermissions(value = {"WORKFLOW:APPROVAL"})
    public R selectUserPhotoAndName(@Valid @RequestBody SelectUserPhotoAndNameForm form) {
        if (!JSONUtil.isJsonArray(form.getIds())) {
            throw new EmosException("参数不是JSON数组");
        }
        List<Integer> param = JSONUtil.parseArray(form.getIds()).toList(Integer.class);
        List<HashMap> list = userService.selectUserPhotoAndName(param);
        return R.ok().put("result", list);
    }

    @PostMapping("/updateUserPhoto")
    @ApiOperation("修改用户头像")
    public R updateUserPhoto(
            @RequestParam("photo") MultipartFile file,
            @RequestHeader("token") String token) {
        if (null == file) {
            return R.error("没有上传文件");
        }
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String fileName = file.getOriginalFilename().toLowerCase();
        try {
            ossClient.putObject(bucketName, dirPrefix + fileName, file.getInputStream());
        } catch (IOException e) {
            throw new EmosException("获取文件输入流错误");
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
        }
        String photoUrl = "https://" + bucketName + "." + endpoint + dirPrefix + fileName;
        int userId = jwtUtil.getUserId(token);
        userService.updateUserPhoto(userId, photoUrl);
        return R.ok(photoUrl);
    }

    @PostMapping("/updateUserInfo")
    @ApiOperation("修改或新增用户信息")
    public R insertOrUpdateUserInfo(@Valid @RequestBody UpdateUserInfoForm form) {
        TbUser tbUser = new TbUser();
        BeanUtil.copyProperties(form, tbUser);
        userService.insertOrUpdateUserInfo(tbUser);
        return R.ok();
    }

    @PostMapping("/deleteUserById")
    @ApiOperation("删除员工")
    @RequiresPermissions(value = {"ROOT", "USER:DELETE"}, logical = Logical.OR)
    public R deleteUserById(@Valid @RequestBody DeleteUserByIdForm form) {
        userService.deleteUserById(form.getId());
        f();
        return R.ok().put("result", "success");
    }

    public void f() {
        System.out.println("hello");
    }
}
