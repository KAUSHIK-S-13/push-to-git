package com.d2d.service;


import com.d2d.dto.MnemonicTypeRequestDTO;
import com.d2d.response.SuccessResponse;
import org.springframework.stereotype.Service;

@Service
public interface MnemonicTypeService {

    SuccessResponse save(MnemonicTypeRequestDTO mnemonicTypeRequestDTO);

    SuccessResponse getAllList();

    SuccessResponse getById(Integer id);

    SuccessResponse deleteById(Integer id);

}