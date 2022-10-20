package com.simplefanc.office.news.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenfan
 */
@ApiModel
@Data
public class SearchNewsContentForm {
    @NotBlank
    private String url;
}