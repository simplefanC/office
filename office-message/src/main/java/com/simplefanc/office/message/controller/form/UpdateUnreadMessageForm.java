package com.simplefanc.office.message.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 未读消息更新成已读消息
 * @author chenfan
 */
@ApiModel
@Data
public class UpdateUnreadMessageForm {
    @NotBlank
    private String id;
}