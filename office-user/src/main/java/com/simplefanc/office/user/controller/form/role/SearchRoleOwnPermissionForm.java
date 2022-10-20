package com.simplefanc.office.user.controller.form.role;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author chenfan
 */
@Data
@ApiModel
public class SearchRoleOwnPermissionForm {
    @NotNull
    @Min(0)
    private Integer id;
}