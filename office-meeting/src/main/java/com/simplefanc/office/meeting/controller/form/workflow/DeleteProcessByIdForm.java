package com.simplefanc.office.meeting.controller.form.workflow;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeleteProcessByIdForm {
    @NotBlank
    private String reason;
    @NotBlank
    private String instanceId;
    @NotBlank
    private String uuid;
}
