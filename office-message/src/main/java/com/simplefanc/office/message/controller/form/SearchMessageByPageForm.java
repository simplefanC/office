package com.simplefanc.office.message.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 获取分页消息列表
 *
 * @author chenfan
 */
@ApiModel
@Data
public class SearchMessageByPageForm {
    @NotNull
    @Min(1)
    private Integer page;
    @NotNull
    @Range(min = 1, max = 40)
    private Integer length;
}