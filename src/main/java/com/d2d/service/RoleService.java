package com.d2d.service;


import com.d2d.dto.RoleRequestDTO;
import com.d2d.response.SuccessResponse;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {

    SuccessResponse save(RoleRequestDTO roleRequestDTO);

    SuccessResponse getAllList();

    SuccessResponse getById(Integer id);

    SuccessResponse deleteById(Integer id);

    String getMenuByRole(Integer id);
}