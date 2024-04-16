package com.d2d.controller;


import com.d2d.exception.CustomValidationException;
import com.d2d.request.UserSignUpRequest;
import com.d2d.response.SuccessResponse;
import com.d2d.service.UserService;
import io.minio.errors.MinioException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/test")
    public String test(){
        return "success";
    }

    @PostMapping ("/userSignup")
    public SuccessResponse<Object> userSignup(@RequestBody UserSignUpRequest userSignUpRequest) throws CustomValidationException {
        return userService.userSignup(userSignUpRequest);
    }

    @GetMapping("/findAll")
    @Operation(summary = "List All Users", security = @SecurityRequirement(name = "loan Authentication"))
    public SuccessResponse<Object> userList(){
        return userService.findAll();
    }


    @PostMapping ("/googleSignup")
    public SuccessResponse<Object> googleSignup(@RequestHeader("Authorization") String token  ) throws CustomValidationException, GeneralSecurityException, IOException {
        return userService.googleSignup(token);
    }

    @GetMapping("/getById")
    public SuccessResponse<Object>  getById(@RequestParam("id") Long id) {
        return userService.getById(id);
    }

    @PostMapping("/upload/profile/Photo")
    public SuccessResponse<Object>  userProfilePhoto(@RequestPart("file") MultipartFile file) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException, CustomValidationException {
        return userService.userProfilePhoto(file);
    }

}
