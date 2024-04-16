package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MnemonicRequestDto {
    private Integer id;
    private String mnemonicType;
    private String masterName;
    private Integer sortOrder;
}
