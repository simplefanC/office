package com.simplefanc.office.checkin.controller.form.config;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class AddOrDeleteWorkdayForm {
    @NotBlank
    private String date;
    @NotBlank
    private String type;
}
