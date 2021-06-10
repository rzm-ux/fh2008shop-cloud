package com.fh.shop.member.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class Member implements Serializable {


    @ApiModelProperty(hidden = true)
    private Long id;
    @ApiModelProperty(value = "会员名")
    private String memberName;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "邮箱")
    private String mail;

    private Integer start;

    private Integer integral;





}
