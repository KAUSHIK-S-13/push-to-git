package com.d2d.dto;


import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
@Getter
@Setter
public class FileDetailsDTO {
    private Integer id;
    private String originalFileName;
    private String generatedFileName;
    private Boolean isActive;
    private Boolean deletedFlag;
    private Timestamp createdAt;
    private Integer createdBy;
    private Timestamp modifiedAt;
    private Integer modifiedBy;
}
