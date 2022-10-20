package com.simplefanc.office.facerecognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1、通过本微服务即可访问flask的接口 localhost:8080/face-recognition/create_face_model
 * 2、∵集成了gateway ∴flask亦可访问nacos的微服务 如在flask中localhost:8083/office-user/user/searchUserSummary?userId=1
 */
@SpringBootApplication
public class OfficeFaceRecognitionApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeFaceRecognitionApplication.class, args);
    }
}
