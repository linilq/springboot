package com.linilq.mybatis.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SysUser extends BaseDo{
    private String userName;
    private String password;
}
