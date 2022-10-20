package com.simplefanc.office.common.feign;

import com.simplefanc.office.common.dto.MessageEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("office-message")
public interface ServiceMessageFeignClient {
    @PostMapping("/message/sendAsync")
    void sendAsync(@RequestParam("topic") String topic, @RequestBody MessageEntity entity);
}