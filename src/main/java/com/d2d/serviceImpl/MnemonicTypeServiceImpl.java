package com.d2d.serviceImpl;


import com.d2d.dto.MnemonicTypeDTO;
import com.d2d.dto.MnemonicTypeRequestDTO;
import com.d2d.entity.MnemonicType;
import com.d2d.entity.Role;
import com.d2d.repository.MnemonicTypeRepository;
import com.d2d.repository.RoleRepository;
import com.d2d.response.SuccessResponse;
import com.d2d.service.MnemonicTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MnemonicTypeServiceImpl implements MnemonicTypeService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MnemonicTypeRepository mnemonicTypeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public SuccessResponse getAllList() {
        SuccessResponse successResponse = new SuccessResponse();
        List<MnemonicTypeDTO> list = new ArrayList<>();
        List<MnemonicType> mnemonicTypeList = mnemonicTypeRepository.findAll();
        if(mnemonicTypeList.size() > 0) {
            mnemonicTypeList.forEach(mnemonicType -> {
                list.add(modelMapper.map(mnemonicType,MnemonicTypeDTO.class));
            });
        }
        successResponse.setData(list);
        return successResponse;
    }

    @Override
    public SuccessResponse getById(Integer id) {
        SuccessResponse successResponse = new SuccessResponse();
        Optional<MnemonicType> mnemonicTypeOptional = mnemonicTypeRepository.findById(id);
        if (mnemonicTypeOptional.isPresent()) {
            if (!mnemonicTypeOptional.get().getDeletedFlag()) { 
                MnemonicTypeDTO mnemonicTypeDTO = modelMapper.map(mnemonicTypeOptional.get(), MnemonicTypeDTO.class);
                successResponse.setData(mnemonicTypeDTO);
            } else {
                throw new RuntimeException("Id not Found Exception");
            }
        } else {
            throw new RuntimeException("Id not Found Exception");
        }
        return successResponse;
            }

    @Override
    public SuccessResponse deleteById(Integer id) {
        SuccessResponse successResponse = new SuccessResponse();
        Optional<MnemonicType> mnemonicTypeOptional = mnemonicTypeRepository.findById(id);
        if (mnemonicTypeOptional.isPresent()) {
            if (!mnemonicTypeOptional.get().getDeletedFlag()) { 
                 mnemonicTypeRepository.deleteById(id);
                 successResponse.setData("Record Deleted Successfully");
            } else {
                throw new RuntimeException("Id not Found Exception");
            }
        } else {
            throw new RuntimeException("Id not Found Exception");
        }
        return successResponse;
            }

    @Override
    public SuccessResponse save(MnemonicTypeRequestDTO mnemonicTypeRequestDTO) {
        SuccessResponse successResponse = new SuccessResponse();
        MnemonicType mnemonicType = null ;
        if (mnemonicTypeRequestDTO.getId() == null) {
            mnemonicType = new MnemonicType();
        } else {
            Optional<MnemonicType> mnemonicTypeOptional = mnemonicTypeRepository.findById(mnemonicTypeRequestDTO.getId());
            if (mnemonicTypeOptional.isPresent()) {
                mnemonicType = mnemonicTypeOptional.get();
            } else {
                throw new RuntimeException("Id not Found Exception");
            }
        }
        Optional<Role> roleOptional = roleRepository.findById(mnemonicTypeRequestDTO.getRoleId());
        if (roleOptional.isPresent()) {
            mnemonicType.setRole(roleOptional.get());
        } else {
            throw new RuntimeException("Id not Found Exception");
        }

        modelMapper.map(mnemonicTypeRequestDTO,mnemonicType);
        mnemonicTypeRepository.save(mnemonicType);
        successResponse.setData("Data Saved Successfully");
        return successResponse;
            }


}