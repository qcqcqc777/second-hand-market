package com.tekram.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.server.ResponseStatusException;
import com.tekram.mapper.AddressMapper;
import com.tekram.pojo.Address;
import com.tekram.pojo.AddressDTO;
import com.tekram.pojo.AddressVO;
import com.tekram.response.PageResult;
import com.tekram.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public PageResult getAddress(Integer page, Integer pageSize, Long userId) {
        PageHelper.startPage(page, pageSize);
        List<AddressVO> addressList = addressMapper.selectAddressById(userId);
        PageInfo<AddressVO> pageInfo = new PageInfo<>(addressList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    @Transactional
    public AddressVO createAddress(Long userId, AddressDTO dto) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        Byte status = addressMapper.selectUserStatusForUpdate(userId);
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        if (status != 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        int count = addressMapper.countAddressesByUserId(userId);
        if (count >= 20) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "LIMIT_EXCEEDED");
        }
        //新地址是否要设为默认
        boolean isDefault = count == 0 || Boolean.TRUE.equals(dto.getDefaultFlag());
        if (isDefault) {
            addressMapper.clearDefaultByUserId(userId);
        }
        Address address = new Address();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);
        address.setDefaultFlag((byte) (isDefault ? 1 : 0));
        if (addressMapper.insertAddress(address) != 1 || address.getId() == null) {
            throw new IllegalStateException("Address insert failed");
        }
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        vo.setId(address.getId().toString());
        vo.setDefaultFlag(Byte.valueOf((byte) 1).equals(address.getDefaultFlag()));
        return vo;
    }

    @Override
    @Transactional
    public AddressVO updateAddress(Long userId, Long addressId, AddressDTO dto) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        Byte status = addressMapper.selectUserStatusForUpdate(userId);
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        if (status != 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        //先检查地址归属，避免无效请求清除当前用户的默认地址
        if (addressMapper.selectOwnedAddressIdForUpdate(userId, addressId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ADDRESS_NOT_FOUND");
        }
        //新地址是否要设为默认
        boolean isDefault =  Boolean.TRUE.equals(dto.getDefaultFlag());
        if (isDefault) {
            addressMapper.clearDefaultByUserId(userId);
        }
        Address address = new Address();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);
        address.setId(addressId);
        address.setDefaultFlag((byte) (isDefault ? 1 : 0));
        if (addressMapper.updateAddress(address) != 1 || address.getId() == null) {
            throw new IllegalStateException("Address update failed");
        }
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        vo.setId(address.getId().toString());
        vo.setDefaultFlag(Byte.valueOf((byte) 1).equals(address.getDefaultFlag()));
        return vo;
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        Byte status = addressMapper.selectUserStatusForUpdate(userId);
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        if (status != 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        if(addressMapper.deleteAddress(addressId, userId) <= 0){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "ADDRESS_NOT_FOUND"
            );
        }
        addressMapper.deleteAddress(addressId, userId);

    }


}
