package com.simplefanc.office.user.controller.form.dept;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class InsertDeptForm {
    @NotBlank
    private String deptName;
}
