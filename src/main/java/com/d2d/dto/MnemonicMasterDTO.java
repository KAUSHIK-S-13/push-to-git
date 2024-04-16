package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MnemonicMasterDTO {
	private Integer id;

	private MnemonicTypeDTO mnemonicType;

	private String masterCode;

	private String masterName;

	private String parentMasterCode;

	private Integer sortOrder;

	private Boolean isActive;

	private Boolean deletedFlag;

	private Timestamp createdAt;

	private Integer createdBy;

	private Timestamp modifiedAt;

	private Integer modifiedBy;

}

