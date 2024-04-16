package com.d2d.service;


import com.d2d.entity.Users;
import com.d2d.exception.CustomValidationException;
import com.d2d.response.AuthResponse;
import com.d2d.response.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
	LoginResponse login(AuthResponse response,String email) throws Exception;

    Users getUserInfo(String userName) throws Exception;

	Users getUser(String username) throws Exception;


	Users getPasswords(String password) throws CustomValidationException;

	Users getEmailValiations(String username) throws CustomValidationException;

	 Users getUsers(String username) throws CustomValidationException;



}
