package com.simplefanc.office.message.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 删除消息
 * @author chenfan
 */
@Data
@ApiModel
public class DeleteMessageRefByIdForm {
    @NotBlank
    private String id;
}