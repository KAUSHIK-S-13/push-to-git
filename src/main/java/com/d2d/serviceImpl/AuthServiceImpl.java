package com.d2d.serviceImpl;


import com.d2d.entity.Users;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.repository.UsersRepository;
import com.d2d.response.AuthResponse;
import com.d2d.response.LoginResponse;
import com.d2d.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LoginResponse login(AuthResponse response,String Email) throws Exception {
        if (response != null) {
            Optional<Users> users = usersRepository.findByEmail(Email);
            if (users.isEmpty( )) {
                throw new Exception("Not Found");
            }
            return new LoginResponse(modelMapper.map(users.get( ), Users.class), response.getToken( ),
                    response.getRefreshToken( ));
        } else
            throw new Exception("Login Failed.!");
    }

    @Override
    public Users getUserInfo(String username) throws Exception {
        Optional<Users> users = usersRepository.findUserNames(username);
        if (users.isEmpty( )) {
            throw new Exception("User Not found");
        }
        return modelMapper.map(users.get( ), Users.class);
    }


    @Override
    public Users getUser(String email) throws CustomValidationException {
        Optional<Users> users = usersRepository.findByEmail(email);
        if (users.isEmpty( )) {
            throw new CustomValidationException(ErrorCode.D2D_25);
        }
        return users.get( );
    }

    @Override
    public Users getUsers(String username) throws CustomValidationException {
        Optional<Users> users = usersRepository.findUserNames(username);
        if (users.isEmpty( )) {
            throw new CustomValidationException(ErrorCode.D2D_25);
        }
        return users.get( );
    }


    @Override
    public Users getPasswords(String password) throws CustomValidationException {
        Optional<Users> users = usersRepository.findByPass(password);
        if (users.isEmpty( )) {
            throw new CustomValidationException(ErrorCode.D2D_26);
        }
        return users.get( );
    }

    @Override
    public Users getEmailValiations(String username) throws CustomValidationException {
        Optional<Users> users = usersRepository.findByEmail(username);
        if (users.isEmpty( )) {
            throw new CustomValidationException(ErrorCode.D2D_27);
        }
        return users.get( );
    }

}
