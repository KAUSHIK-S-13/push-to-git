package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataTypeMappingDTO {

    private List<DataTypeDTO> dataTypeList;

    private List<MappingDTO> mappingList;


}
