package com.d2d.serviceImpl;


import com.d2d.dto.MnemonicMasterDTO;
import com.d2d.dto.MnemonicMasterRequestDTO;
import com.d2d.dto.MnemonicRequestDto;
import com.d2d.entity.MnemonicMaster;
import com.d2d.entity.MnemonicType;
import com.d2d.repository.MnemonicMasterRepository;
import com.d2d.repository.MnemonicTypeRepository;
import com.d2d.response.SuccessResponse;
import com.d2d.service.MnemonicMasterService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MnemonicMasterServiceImpl implements MnemonicMasterService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MnemonicMasterRepository mnemonicMasterRepository;

    @Autowired
    private MnemonicTypeRepository mnemonicTypeRepository;

    @Override
    public SuccessResponse getAllList() {
        SuccessResponse successResponse = new SuccessResponse();
        List<MnemonicMasterDTO> list = new ArrayList<>();
        List<MnemonicMaster> mnemonicMasterList = mnemonicMasterRepository.findAll();
        if(mnemonicMasterList.size() > 0) {
            mnemonicMasterList.forEach(mnemonicMaster -> {
                list.add(modelMapper.map(mnemonicMaster,MnemonicMasterDTO.class));
            });
        }
        successResponse.setData(list);
        return successResponse;
    }

    @Override
    public SuccessResponse getById(Integer id) {
        SuccessResponse successResponse = new SuccessResponse();
        Optional<MnemonicMaster> mnemonicMasterOptional = mnemonicMasterRepository.findById(id);
        if (mnemonicMasterOptional.isPresent()) {
            if (!mnemonicMasterOptional.get().getDeletedFlag()) { 
                MnemonicMasterDTO mnemonicMasterDTO = modelMapper.map(mnemonicMasterOptional.get(), MnemonicMasterDTO.class);
                successResponse.setData(mnemonicMasterDTO);
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
        Optional<MnemonicMaster> mnemonicMasterOptional = mnemonicMasterRepository.findById(id);
        if (mnemonicMasterOptional.isPresent()) {
            if (!mnemonicMasterOptional.get().getDeletedFlag()) { 
                 mnemonicMasterRepository.deleteById(id);
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
    public SuccessResponse save(MnemonicMasterRequestDTO mnemonicMasterRequestDTO) {
        SuccessResponse successResponse = new SuccessResponse();
        MnemonicMaster mnemonicMaster = null ;
        if (mnemonicMasterRequestDTO.getId() == null) {
            mnemonicMaster = new MnemonicMaster();
        } else {
            Optional<MnemonicMaster> mnemonicMasterOptional = mnemonicMasterRepository.findById(mnemonicMasterRequestDTO.getId());
            if (mnemonicMasterOptional.isPresent()) {
                mnemonicMaster = mnemonicMasterOptional.get();
            } else {
                throw new RuntimeException("Id not Found Exception");
            }
        }
        Optional<MnemonicType> mnemonicTypeOptional = mnemonicTypeRepository.findById(mnemonicMasterRequestDTO.getMnemonicTypeId());
        if (mnemonicTypeOptional.isPresent()) {
            mnemonicMaster.setMnemonicType(mnemonicTypeOptional.get());
        } else {
            throw new RuntimeException("Id not Found Exception");
        }

        modelMapper.map(mnemonicMasterRequestDTO,mnemonicMaster);
        mnemonicMasterRepository.save(mnemonicMaster);
        successResponse.setData("Data Saved Successfully");
        return successResponse;
            }

    @Override
    public SuccessResponse<Object> getMnemonicMasterByNameAndId(Integer typeId, String masterName) {
        SuccessResponse<Object> successResponse = new SuccessResponse<>();
        List<MnemonicRequestDto> list = new ArrayList<>();
        List<MnemonicMaster> mnemonicMasterList = null;
        if (masterName!=null) {
            mnemonicMasterList = mnemonicMasterRepository.findByMnemonicName(masterName);
        } else if (typeId != null) {
            mnemonicMasterList = mnemonicMasterRepository.findByTypeId(typeId);
        }

        if (mnemonicMasterList != null && !mnemonicMasterList.isEmpty()) {
            for (MnemonicMaster data : mnemonicMasterList) {
                MnemonicRequestDto mnemonicRequestDto = new MnemonicRequestDto();
                mnemonicRequestDto.setId(data.getId());
                mnemonicRequestDto.setMnemonicType(data.getMnemonicType().getTypeName());
                mnemonicRequestDto.setMasterName(data.getMasterName());
                mnemonicRequestDto.setSortOrder(data.getSortOrder());
                list.add(mnemonicRequestDto);
            }
            successResponse.setData(list);
        } else {
            throw new RuntimeException("Data Not Found");
        }
        return successResponse;
    }

}