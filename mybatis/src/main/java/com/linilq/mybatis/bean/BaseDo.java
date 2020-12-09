package com.linilq.mybatis.bean;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class BaseDo {

    private Integer id;
    private Integer isDelete;
    private String createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;


}
