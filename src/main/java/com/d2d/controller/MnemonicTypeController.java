package com.d2d.controller;


import com.d2d.dto.MnemonicTypeRequestDTO;
import com.d2d.response.SuccessResponse;
import com.d2d.service.MnemonicTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mnemonicType")
public class MnemonicTypeController { 

    @Autowired
    private MnemonicTypeService mnemonicTypeService;

    @PostMapping("/save")
    public SuccessResponse save(@RequestBody MnemonicTypeRequestDTO mnemonicTypeRequestDTO) {
        return mnemonicTypeService.save(mnemonicTypeRequestDTO);
    }

    @GetMapping("/getAll")
    public SuccessResponse getAllList() {
        return mnemonicTypeService.getAllList();
    }

    @GetMapping("/getById")
    public SuccessResponse getById(@RequestParam("id") Integer id) {
        return mnemonicTypeService.getById(id);
    }

    @DeleteMapping("/deleteById")
    public SuccessResponse deleteById(@RequestParam("id") Integer id) {
        return mnemonicTypeService.deleteById(id);
    }

}