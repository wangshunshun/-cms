package com.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper {
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
    void deleteByUserId(@Param("userId") Long userId);
    void insertBatch(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
