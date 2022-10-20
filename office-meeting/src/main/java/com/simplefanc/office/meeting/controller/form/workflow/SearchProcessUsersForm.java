package com.simplefanc.office.meeting.controller.form.workflow;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SearchProcessUsersForm {
    @NotBlank
    private String instanceId;
}
