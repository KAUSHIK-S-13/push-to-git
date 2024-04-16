package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteDTO {

    private Boolean isChecked = false;

    private Boolean softDelete = false;

    private Boolean hardDelete = false;
}
