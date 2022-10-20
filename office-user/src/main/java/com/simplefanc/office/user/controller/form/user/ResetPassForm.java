package com.simplefanc.office.user.controller.form.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResetPassForm {
    @NotBlank
    private String oldPass;
    @NotBlank
    private String newPass;
}
