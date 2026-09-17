package com.tekram.mapper;

import com.tekram.pojo.Address;
import com.tekram.pojo.AddressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {
    //查询地址列表
    List<AddressVO> selectAddressById(@Param("userId") Long userId);
    //查询用户状态
    Byte selectUserStatusForUpdate(@Param("userId") Long userId);
    //查询地址数
    int countAddressesByUserId(@Param("userId") Long userId);
    //按地址 ID 和用户 ID 检查归属，并锁定地址行
    Long selectOwnedAddressIdForUpdate(@Param("userId") Long userId,
                                       @Param("addressId") Long addressId);
    //取消默认
    int clearDefaultByUserId(@Param("userId") Long userId);
    //新增地址
    int insertAddress(Address address);
    //修改地址
    int updateAddress(Address address);
    //删除地址
    int deleteAddress(@Param("id") Long id,@Param("userId") Long userId);
}
