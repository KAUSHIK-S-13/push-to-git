package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MnemonicTypeDTO {
	private Integer id;

	private String typeCode;

	private String typeName;

	private RoleDTO role;

	private String parentTypeCode;

	private Boolean isActive;

	private Boolean deletedFlag;

	private Timestamp createdAt;

	private Integer createdBy;

	private Timestamp modifiedAt;

	private Integer modifiedBy;

}

