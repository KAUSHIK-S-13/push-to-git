package com.d2d.service;


import com.d2d.dto.MnemonicMasterRequestDTO;
import com.d2d.response.SuccessResponse;
import org.springframework.stereotype.Service;

@Service
public interface MnemonicMasterService {

    SuccessResponse save(MnemonicMasterRequestDTO mnemonicMasterRequestDTO);

    SuccessResponse getAllList();

    SuccessResponse getById(Integer id);

    SuccessResponse deleteById(Integer id);

    SuccessResponse<Object> getMnemonicMasterByNameAndId(Integer typeId, String masterName);
}