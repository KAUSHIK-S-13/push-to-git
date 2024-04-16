package com.d2d.response;

import com.d2d.entity.Users;
import lombok.Getter;

@Getter
public class LoginResponse {
    private Users users;

    private String token;

    private String refreshToken;

    public LoginResponse(Users users, String token, String refreshToken) {
        this.users = users;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
