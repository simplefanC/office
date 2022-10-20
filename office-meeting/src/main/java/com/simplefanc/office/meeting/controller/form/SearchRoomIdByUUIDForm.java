package com.simplefanc.office.meeting.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 接受移动端提交的UUID
 * @author chenfan
 */
@Data
@ApiModel
public class SearchRoomIdByUUIDForm {
    @NotBlank
    private String uuid;
}