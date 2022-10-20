package com.simplefanc.office.user.controller.form.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class DeleteUserByIdForm {
    @NotNull
    private Integer id;
}
