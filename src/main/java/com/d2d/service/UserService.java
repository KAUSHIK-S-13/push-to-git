package com.d2d.service;


import com.d2d.exception.CustomValidationException;
import com.d2d.request.UserSignUpRequest;
import com.d2d.response.SuccessResponse;
import io.minio.errors.MinioException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public interface UserService {
    SuccessResponse<Object> userSignup(UserSignUpRequest usersDTO) throws CustomValidationException;

    SuccessResponse<Object> findAll();

    SuccessResponse<Object> googleSignup(String token) throws GeneralSecurityException, IOException;

    SuccessResponse<Object> getById(Long id);

    SuccessResponse<Object> userProfilePhoto(MultipartFile file) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException, CustomValidationException;
}
