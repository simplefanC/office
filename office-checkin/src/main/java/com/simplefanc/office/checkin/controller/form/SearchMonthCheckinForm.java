package com.simplefanc.office.checkin.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author chenfan
 */
@Data
@ApiModel
public class SearchMonthCheckinForm {
    @NotNull
    @Range(min = 2000, max = 3000)
    private Integer year;
    @NotNull
    @Range(min = 1, max = 12)
    private Integer month;
}