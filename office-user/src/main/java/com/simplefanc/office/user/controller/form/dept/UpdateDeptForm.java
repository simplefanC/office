package com.simplefanc.office.user.controller.form.dept;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class UpdateDeptForm {
    @NotNull
    private Integer id;
    @NotBlank
    private String deptName;
}
