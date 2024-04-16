package com.d2d.serviceImpl;


import com.d2d.config.MinioFileUploader;
import com.d2d.constant.Constant;
import com.d2d.dto.FileDetailsDTO;
import com.d2d.dto.UserLoginDTO;
import com.d2d.dto.UsersDTO;
import com.d2d.entity.Role;
import com.d2d.entity.UserProfile;
import com.d2d.entity.Users;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.repository.RoleRepository;
import com.d2d.repository.UserProfileRepository;
import com.d2d.repository.UsersRepository;
import com.d2d.request.UserSignUpRequest;
import com.d2d.response.SuccessResponse;
import com.d2d.securityConfig.JwtRequestFilter;
import com.d2d.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MinioFileUploader minioFileUploader;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Value("${google.key}")
    private String googleKey;


    @Override
    public SuccessResponse<Object> userSignup(UserSignUpRequest userSignUpRequest) throws CustomValidationException {
        SuccessResponse<Object> successResponse = new SuccessResponse<>();

        if (userSignUpRequest.getUserId() == null || userSignUpRequest.getUserId() == 0) {
            if (userExits(userSignUpRequest)) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                Users users = modelMapper.map(userSignUpRequest, Users.class);
                users.setIsActive(true);
                users.setDeleteFlag(false);
                Optional<Role> role=roleRepository.findById(1);
                users.setRole(role.get());
                users.setCreatedBy(userSignUpRequest.getUsername());
                users.setPassword(bCryptPasswordEncoder.encode(users.getPassword()));
                usersRepository.saveAndFlush(users);
            } else {
                throw new CustomValidationException(ErrorCode.D2D_24);
            }
        } else {
            Optional<Users> users = usersRepository.findById(userSignUpRequest.getUserId());
            if (users.isPresent()) {
                users.get().setEmail(userSignUpRequest.getEmail());
                users.get().setUsername(userSignUpRequest.getUsername());
                users.get().setMobileNumber(userSignUpRequest.getMobileNumber());
                users.get().setAddress(userSignUpRequest.getAddress());
                users.get().setDateOfBirth(userSignUpRequest.getDateOfBirth());
                usersRepository.save(users.get());
            }
        } successResponse.setData(Constant.USER_CREATED_SUCCESSFULLY);
        return successResponse;
    }

    @Override
    public SuccessResponse<Object> findAll() {
        SuccessResponse<Object> successResponse = new SuccessResponse<>( );
        List<Users> users = usersRepository.findAll( );
        List<UsersDTO> usersDTOS = Arrays.asList(modelMapper.map(users, UsersDTO[].class));
        successResponse.setData(usersDTOS);
        return successResponse;
    }

    @Override
    public SuccessResponse<Object> googleSignup(String token) throws GeneralSecurityException, IOException {
        SuccessResponse<Object> successResponse = new SuccessResponse<>();
        String tokenWithoutBearer = token.replace("Bearer ", "");

        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleKey))
                .build();
        GoogleIdToken idToken = verifier.verify(tokenWithoutBearer);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            UserLoginDTO userLoginDTO = new UserLoginDTO();
            Optional<Users> usersOptional = usersRepository.findByEmail(email);
            if (usersOptional.isEmpty()) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                Users users = new Users();
                users.setIsActive(true);
                users.setDeleteFlag(false);
                users.setCreatedBy("admin");
                users.setEmail(email);
                users.setUsername(name);
                users.setProvider("google");
                Optional<Role> role=roleRepository.findById(1);
                users.setRole(role.get());
                users.setPassword(bCryptPasswordEncoder.encode("Admin@123"));
                Users user = usersRepository.save(users);
                userLoginDTO.setId(user.getId());
                userLoginDTO.setUsername(user.getUsername());
                userLoginDTO.setEmail(user.getEmail());
                userLoginDTO.setRole(user.getRole().getRole());
                userLoginDTO.setRoleId(user.getRole().getId());
                userLoginDTO.setRoleJson(user.getRole().getMenuData());

            } else {
                userLoginDTO.setId(usersOptional.get().getId());
                userLoginDTO.setUsername(usersOptional.get().getUsername());
                userLoginDTO.setEmail(usersOptional.get().getEmail());
                userLoginDTO.setRole(usersOptional.get().getRole().getRole());
                userLoginDTO.setRoleId(usersOptional.get().getRole().getId());
                userLoginDTO.setRoleJson(usersOptional.get().getRole().getMenuData());
            }
            successResponse.setData(userLoginDTO);
        }
        return successResponse;
    }

    @Override
    public SuccessResponse<Object> getById(Long id) {
        SuccessResponse<Object> successResponse = new SuccessResponse<>();
        Optional<Users> users = usersRepository.findById(id);
        if (users.isPresent()) {
            if (!users.get().getDeleteFlag()) {
                UsersDTO usersDTO = modelMapper.map(users.get(), UsersDTO.class);
                Optional<UserProfile> userProfile=userProfileRepository.findByIdCreatedBy(id);
                userProfile.ifPresent(profile ->
                        usersDTO.setProfileDetails(modelMapper.map(userProfile.get(), FileDetailsDTO.class)));
                successResponse.setData(usersDTO);
            } else {
                throw new RuntimeException("Id not Found Exception");
            }
        } else {
            throw new RuntimeException("Id not Found Exception");
        }
        return successResponse;
    }

    @Override
    public SuccessResponse<Object> userProfilePhoto(MultipartFile file) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException, CustomValidationException {
        Optional<UserProfile> userProfile = userProfileRepository.findByIdCreatedBy(JwtRequestFilter.userId.get());
        userProfile.ifPresent(profile -> userProfileRepository.deleteById(profile.getId()));
        Map<String, String> files = minioFileUploader.uploadFile(file);
        UserProfile fileDetails = new UserProfile();
        fileDetails.setOriginalFileName(files.get(Constant.ORIGINAL_FILE_NAME));
        fileDetails.setGeneratedFileName(files.get(Constant.GENERATED_FILE_NAME));
        fileDetails.setIsActive(true);
        fileDetails.setDeletedFlag(false);
        fileDetails.setCreatedBy(JwtRequestFilter.userId.get());
        fileDetails.setModifiedBy(JwtRequestFilter.userId.get());
        UserProfile fileDetailsRes = userProfileRepository.save(fileDetails);
        SuccessResponse<Object> successResponse = new SuccessResponse<>();
        successResponse.setData(fileDetailsRes);
        return successResponse;
    }

    public Boolean userExits(UserSignUpRequest userSignUpRequest) throws CustomValidationException {
        Boolean isExist = true;
        Optional<Users> users = usersRepository.findUserName(userSignUpRequest.getEmail( ));
        if (users.isPresent()) {
            throw new CustomValidationException(ErrorCode.D2D_24);
        }
        return isExist;
    }

}
