package com.d2d.dto;

import com.d2d.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersDTO {
	private Integer id;

	private String username;

	private String password;

	private String email;

	private String mobileNumber;

	private String panNumber;

	private String provider;

	private Role role;

	private FileDetailsDTO profileDetails ;




}

