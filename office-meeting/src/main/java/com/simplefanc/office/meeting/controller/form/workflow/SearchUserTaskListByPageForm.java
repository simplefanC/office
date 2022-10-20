package com.simplefanc.office.meeting.controller.form.workflow;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SearchUserTaskListByPageForm {
    @NotNull
    @Min(1L)
    private Integer page;
    @NotBlank
    private String type;
    @NotNull
    @Range(min = 1L, max = 60L)
    private Integer length;
}
