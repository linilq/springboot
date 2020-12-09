package com.linilq.mybatis.controller;

import com.linilq.mybatis.bean.SysUser;
import com.linilq.mybatis.dao.SysUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    SysUserDao sysUserDao;

    @RequestMapping("getUser")
    @ResponseBody
    public String getUser(@RequestParam String username) {
        SysUser sysUser = sysUserDao.getSysUserByName(username);

        return sysUser.toString();
    }
}
