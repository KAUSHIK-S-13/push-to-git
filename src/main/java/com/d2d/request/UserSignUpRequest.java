package com.d2d.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequest {
    private String username;

    private String password;

    private String email;

    private String mobileNumber;

    private String panNumber;

    private String address;

    private String dateOfBirth;

    private Long userId;
}
