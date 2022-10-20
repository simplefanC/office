package com.simplefanc.office.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("office-face-recognition")
public interface FaceRecognitionFeignClient {
    @PostMapping("/create_face_model")
    String createFaceModel(@RequestParam("path") String path);

    @PostMapping("/check_in")
    String checkin(@RequestParam("path") String path, @RequestParam("targetModel") String faceModel);
}