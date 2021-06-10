package com.fh.shop.api;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.common.Constants;
import com.fh.shop.api.member.vo.MemberVo;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BaseController {

    public MemberVo buildmemberVo(HttpServletRequest request){
        try {
            String header = URLDecoder.decode(request.getHeader(Constants.SECRET),"utf-8");
            MemberVo memberVo = JSON.parseObject(header, MemberVo.class);
            return memberVo;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
