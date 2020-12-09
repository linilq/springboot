package com.linilq.mybatis.dao;

import com.linilq.mybatis.bean.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserDao {


    SysUser getSysUserByName(@Param("userName") String userName);
}
