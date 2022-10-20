package com.simplefanc.office.common.feign;

import com.simplefanc.office.common.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@FeignClient("office-user")
public interface ServiceUserFeignClient {
    @GetMapping("/user/searchById/{userId}")
    UserDTO searchById(@PathVariable("userId") Integer userId);

    @GetMapping("/user/searchUserHiredate/{userId}")
    String searchUserHiredate(@PathVariable("userId") Integer userId);

    @GetMapping("/user/searchUserInfo/{userId}")
    HashMap<String, Object> searchUserInfo(@PathVariable("userId") Integer userId);

    @GetMapping("/user/searchDeptManagerId/{userId}")
    Integer searchDeptManagerId(@PathVariable("userId") Integer userId);

    @GetMapping("/user/searchGmId")
    Integer searchGmId();

    @GetMapping("/user/searchNameAndDept/{userId}")
    HashMap<String, String> searchNameAndDept(@PathVariable("userId") int userId);
}