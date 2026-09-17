package com.tekram.service;

import com.tekram.pojo.AddressDTO;
import com.tekram.pojo.AddressVO;
import com.tekram.response.PageResult;
import org.apache.ibatis.annotations.Param;

public interface AddressService {
    /*
   查询地址列表
    */
    PageResult getAddress(Integer page, Integer pageSize, Long id);
    /*
    新增地址
     */
    AddressVO createAddress(Long userId, AddressDTO dto);
    /*
    修改地址
     */
    AddressVO updateAddress(Long userId, Long addressId,AddressDTO dto);
    /*
    删除地址
     */
    void deleteAddress(@Param("id") Long id, @Param("userId") Long userId);
}
