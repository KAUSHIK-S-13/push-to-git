package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnableSecurityDTO {

    private Boolean isSecurityRequired = false;

    private Integer securityMethod;

    private String authTableName;

    private String fieldOne;

    private String fieldTwo;
}
