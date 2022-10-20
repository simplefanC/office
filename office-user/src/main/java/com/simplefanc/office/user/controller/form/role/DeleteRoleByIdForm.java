package com.simplefanc.office.user.controller.form.role;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class DeleteRoleByIdForm {
    @NotNull
    private Integer id;
}
