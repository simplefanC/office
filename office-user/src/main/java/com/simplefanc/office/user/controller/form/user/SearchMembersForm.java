package com.simplefanc.office.user.controller.form.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenfan
 */
@Data
@ApiModel
public class SearchMembersForm {
    @NotBlank
    private String members;
}