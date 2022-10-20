package com.simplefanc.office.common.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author chenfan
 */
@Data
@Component//spring管理 默认全局单例
public class SystemConstants {
    public String attendanceStartTime;
    public String attendanceTime;
    public String attendanceEndTime;
    public String closingStartTime;
    public String closingTime;
    public String closingEndTime;
}
