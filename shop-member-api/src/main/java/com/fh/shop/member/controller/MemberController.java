package com.fh.shop.member.controller;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.common.Constants;
import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.member.biz.IMemberService;
import com.fh.shop.member.vo.MemberVo;
import com.fh.shop.util.RedisUtil;
import com.fh.shop.util.UUIDUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;


@RestController
@RequestMapping("/api")
@Api(tags = "巡查管理系统", description = "巡查管理系统模块相关接口")
public class MemberController {

    @Resource(name = "IMemberService")
    private IMemberService iMemberService;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;

    @GetMapping("/meber/login")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName",value = "会员名",dataType ="java.lang.String", required = true),
            @ApiImplicitParam(name = "password",value = "密码",dataType ="java.lang.String", required = true)
    })
    @ApiOperation(value = "会员登录")
    public ServerResponse login(String memberName, String password){
        return iMemberService.login(memberName,password);
    }
    @GetMapping("/meber/logut")
    public ServerResponse logut() throws UnsupportedEncodingException {
        //将request中的信息费返回给前台
//        MemberVo memberVo= (MemberVo) request.getAttribute(Constants.SECRET);
        String header = URLDecoder.decode(request.getHeader(Constants.SECRET),"utf-8");
        MemberVo memberVo = JSON.parseObject(header, MemberVo.class);
        RedisUtil.delete(KeyUtil.builMemberKey(memberVo.getId()));
        return ServerResponse.success(memberVo);
    }
    @GetMapping("/meber/findMember")
    @ApiImplicitParam(name = "x-auth",value = "头信息",paramType = "header",required = true,dataType = "java.lang.String")
    public ServerResponse findMember() throws UnsupportedEncodingException {
        //将request中的信息费返回给前台
        //MemberVo memberVo= (MemberVo) request.getAttribute(Constants.SECRET);
        String header = URLDecoder.decode(request.getHeader(Constants.SECRET),"utf-8");
        MemberVo memberVo = JSON.parseObject(header, MemberVo.class);
        return ServerResponse.success(memberVo);
    }
}
