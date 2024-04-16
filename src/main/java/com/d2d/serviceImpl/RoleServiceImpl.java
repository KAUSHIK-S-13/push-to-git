package com.d2d.serviceImpl;


import com.d2d.dto.RoleDTO;
import com.d2d.dto.RoleRequestDTO;
import com.d2d.entity.Role;
import com.d2d.repository.RoleRepository;
import com.d2d.response.SuccessResponse;
import com.d2d.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public SuccessResponse getAllList() {
        SuccessResponse successResponse = new SuccessResponse();
        List<RoleDTO> list = new ArrayList<>();
        List<Role> roleList = roleRepository.findAll();
        if(roleList.size() > 0) {
            roleList.forEach(role -> {
                list.add(modelMapper.map(role,RoleDTO.class));
            });
        }
        successResponse.setData(list);
        return successResponse;
    }

    @Override
    public SuccessResponse getById(Integer id) {
        SuccessResponse successResponse = new SuccessResponse();
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            if (!roleOptional.get().getDeletedFlag()) { 
                RoleDTO roleDTO = modelMapper.map(roleOptional.get(), RoleDTO.class);
                successResponse.setData(roleDTO);
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
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            if (!roleOptional.get().getDeletedFlag()) { 
                 roleRepository.deleteById(id);
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
    public SuccessResponse save(RoleRequestDTO roleRequestDTO) {
        SuccessResponse successResponse = new SuccessResponse();
        Role role = null ;
        if (roleRequestDTO.getId() == null) {
            role = new Role();
        } else {
            Optional<Role> roleOptional = roleRepository.findById(roleRequestDTO.getId());
            if (roleOptional.isPresent()) {
                role = roleOptional.get();
            } else {
                throw new RuntimeException("Id not Found Exception");
            }
        }
        modelMapper.map(roleRequestDTO,role);
        roleRepository.save(role);
        successResponse.setData("Data Saved Successfully");
        return successResponse;
            }

    @Override
    public String getMenuByRole(Integer id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        String menuData = null;
        if (roleOptional.isPresent()){
            menuData= roleOptional.get().getMenuData();
        }
        return menuData;
    }
}