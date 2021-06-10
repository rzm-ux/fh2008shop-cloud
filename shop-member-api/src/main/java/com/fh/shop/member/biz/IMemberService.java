package com.fh.shop.member.biz;


import com.fh.shop.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;

public interface IMemberService {

    ServerResponse login(String memberName, String password);

}
