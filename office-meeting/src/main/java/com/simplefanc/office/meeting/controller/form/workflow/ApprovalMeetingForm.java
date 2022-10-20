package com.simplefanc.office.meeting.controller.form.workflow;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ApprovalMeetingForm {
    @NotBlank
    private String taskId;
    @NotBlank
    @Pattern(regexp = "^\u540c\u610f$|^\u4e0d\u540c\u610f$")
    private String approval;
}
