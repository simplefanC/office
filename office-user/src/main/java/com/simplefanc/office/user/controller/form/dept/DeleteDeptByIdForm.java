package com.simplefanc.office.user.controller.form.dept;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class DeleteDeptByIdForm {
    @NotNull
    private Integer id;
}
