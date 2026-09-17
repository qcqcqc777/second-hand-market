package com.tekram.controller;

import com.tekram.exception.UnknownFieldException;
import com.tekram.pojo.AddressDTO;
import com.tekram.response.PageResult;
import com.tekram.response.Result;
import com.tekram.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/users/me/addresses")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @GetMapping
    public Result listAddress(@RequestAttribute("currentUserId") Long userId,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("listAddress:page={}, pageSize={}", page, pageSize);
        PageResult list = addressService.getAddress(page,pageSize,userId);
        return Result.success(list);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result createAddress(@RequestAttribute("currentUserId") Long userId,
                                @RequestBody AddressDTO dto, HttpServletRequest request) {
        //检查 URL 有没有额外参数
        if (!request.getParameterMap().isEmpty()) {
            throw new UnknownFieldException(request.getParameterNames().nextElement());
        }
        return Result.success(addressService.createAddress(userId, dto));
    }

    @PutMapping("/{addressId}")
    public Result updateAddress(@RequestAttribute("currentUserId") Long userId,
                                @RequestBody AddressDTO dto,
                                @PathVariable("addressId") Long addressId) {

        return Result.success(addressService.updateAddress(userId, addressId, dto));
    }

    @DeleteMapping("/{addressId}")
    public Result deleteAddress(@RequestAttribute("currentUserId") Long userId,
                                @PathVariable("addressId") Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return Result.success();
    }



}
