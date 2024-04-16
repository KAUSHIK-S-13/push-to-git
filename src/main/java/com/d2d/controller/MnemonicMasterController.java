package com.d2d.controller;


import com.d2d.dto.MnemonicMasterRequestDTO;
import com.d2d.response.SuccessResponse;
import com.d2d.service.MnemonicMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mnemonicMaster")
public class MnemonicMasterController { 

    @Autowired
    private MnemonicMasterService mnemonicMasterService;

    @PostMapping("/save")
    public SuccessResponse save(@RequestBody MnemonicMasterRequestDTO mnemonicMasterRequestDTO) {
        return mnemonicMasterService.save(mnemonicMasterRequestDTO);
    }

    @GetMapping("/getAll")
    public SuccessResponse getAllList() {
        return mnemonicMasterService.getAllList();
    }

    @GetMapping("/getById")
    public SuccessResponse getById(@RequestParam("id") Integer id) {
        return mnemonicMasterService.getById(id);
    }

    @DeleteMapping("/deleteById")
    public SuccessResponse deleteById(@RequestParam("id") Integer id) {
        return mnemonicMasterService.deleteById(id);
    }

    @GetMapping("/getMnemonicMasterByNameAndId")
    public SuccessResponse<Object> getMnemonicMasterByNameAndId(@RequestParam(value = "typeId",required = false) Integer typeId,
                                                                @RequestParam(value = "masterName",required = false) String masterName) {
        return mnemonicMasterService.getMnemonicMasterByNameAndId(typeId,masterName);
    }

}