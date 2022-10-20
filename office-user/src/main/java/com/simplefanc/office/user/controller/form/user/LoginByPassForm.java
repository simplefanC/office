package com.simplefanc.office.user.controller.form.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenfan
 */
@Data
@ApiModel
public class LoginByPassForm {
    @NotBlank(message = "工号不能为空")
    private String id;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String openId;
}
