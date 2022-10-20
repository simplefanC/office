package com.simplefanc.office.meeting.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author chenfan
 */
@ApiModel
@Data
public class DeleteMeetingByIdForm {
    @NotNull
    @Min(1)
    private Integer id;
}