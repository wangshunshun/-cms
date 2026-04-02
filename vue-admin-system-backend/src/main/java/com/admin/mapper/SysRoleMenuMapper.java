package com.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper {
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
    void deleteByRoleId(@Param("roleId") Long roleId);
    void insertBatch(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
}
