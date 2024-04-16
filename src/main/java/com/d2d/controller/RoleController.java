package com.d2d.controller;


import com.d2d.dto.RoleRequestDTO;
import com.d2d.response.SuccessResponse;
import com.d2d.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController { 

    @Autowired
    private RoleService roleService;

    @PostMapping("/save")
    public SuccessResponse save(@RequestBody RoleRequestDTO roleRequestDTO) {
        return roleService.save(roleRequestDTO);
    }

    @GetMapping("/getAll")
    public SuccessResponse getAllList() {
        return roleService.getAllList();
    }

    @GetMapping("/getById")
    public SuccessResponse getById(@RequestParam("id") Integer id) {
        return roleService.getById(id);
    }

    @DeleteMapping("/deleteById")
    public SuccessResponse deleteById(@RequestParam("id") Integer id) {
        return roleService.deleteById(id);
    }

    @GetMapping("/getMenu")
    public String getMenuByRole(@RequestParam("id") Integer id){
        return roleService.getMenuByRole(id);
    }
}