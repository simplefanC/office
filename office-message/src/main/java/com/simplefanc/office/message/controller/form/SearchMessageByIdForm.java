package com.simplefanc.office.message.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 根据ID查询消息
 * @author chenfan
 */
@ApiModel
@Data
public class SearchMessageByIdForm {
    @NotBlank
    private String id;
}