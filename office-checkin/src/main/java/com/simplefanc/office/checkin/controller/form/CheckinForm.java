package com.simplefanc.office.checkin.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author chenfan
 */
@Data
@ApiModel
public class CheckinForm {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
}
