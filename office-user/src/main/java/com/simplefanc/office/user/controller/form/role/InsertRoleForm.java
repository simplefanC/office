package com.simplefanc.office.user.controller.form.role;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class InsertRoleForm {
    @NotBlank
    private String roleName;
    @NotBlank
    private String permissions;
}