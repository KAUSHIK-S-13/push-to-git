package com.d2d.controller;


import com.d2d.entity.Users;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.request.LoginRequest;
import com.d2d.response.AuthResponse;
import com.d2d.response.LoginResponse;
import com.d2d.securityConfig.JWTUtils;
import com.d2d.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) throws Exception {
        if (loginRequest == null) {
            throw new CustomValidationException(ErrorCode.D2D_28);
        }
        AuthResponse response = authenticate(loginRequest.getEmail( ), loginRequest.getPassword( ));
        return authService.login(response,loginRequest.getEmail());
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestParam String token) throws Exception {
        String userName = jwtUtils.getUsernameFromToken(token);
        if (userName == null) {
            throw new CustomValidationException(ErrorCode.D2D_28);
        }
        Users users = authService.getUserInfo(userName);
        if (users == null) {
            throw new CustomValidationException(ErrorCode.D2D_28);
        }
        return getRefreshInfo(userName);
    }


    public AuthResponse authenticate(String username, String password) throws Exception {
        String token = null;
        String refreshToken = null;

        if (username != null) {
            authService.getEmailValiations(username);

        }
        try {
            final Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext( ).setAuthentication(authentication);
            if (Objects.nonNull(authentication)) {
                Users users = authService.getUser(username);
                token = jwtUtils.generateToken(authentication, users);
                refreshToken = jwtUtils.refreshToken(token, users);
            }
        } catch (DisabledException e) {
            throw new CustomValidationException(ErrorCode.D2D_29);
        } catch (BadCredentialsException e) {
            throw new CustomValidationException(ErrorCode.D2D_30);
        }


        return new AuthResponse(token, refreshToken, jwtUtils.getUsernameFromToken(token));

    }

    public AuthResponse getRefreshInfo(String username) throws CustomValidationException {
        String token = null;
        String refreshToken = null;

        try {
            if (Objects.nonNull(username)) {
                Users users = authService.getUsers(username);
                if (users != null) {
                    Map<String, Object> claims = new HashMap<>( );
                    token = jwtUtils.getAccessToken(claims, username);
                    refreshToken = jwtUtils.refreshToken(token, users);
                }
            }
        } catch (DisabledException e) {
            throw new CustomValidationException(ErrorCode.D2D_31);
        } catch (BadCredentialsException e) {
            throw new CustomValidationException(ErrorCode.D2D_30);
        }
        return new AuthResponse(token, refreshToken, jwtUtils.getUsernameFromToken(token));
    }
}