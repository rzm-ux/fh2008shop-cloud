package com.fh.shop.filter;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.common.Constants;
import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class JwtFilter extends ZuulFilter {

    @Value("${fh.shop.checkUrls}")
    private List<String> checkUrls;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @SneakyThrows
    @Override
    public Object run() throws ZuulException {
    log.info("======={}",checkUrls);
    //获取当前访问的URL
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        boolean isCheck=false;
        for (String checkUrl : checkUrls) {
            if (requestURL.indexOf(checkUrl)>0){

                isCheck=true;
                break;
            }
        }
        if (!isCheck){
            //默认相当放行
            return null;
        }
        //需要验证
        //判断请求头是否为空【eyJpZCI6MSwibmlja05hbWUiOiJycnIifQ==.RFE0NkE1RDRRRTQxMjU2QUQ=】
        //自定义头像信息
        String header = request.getHeader("x-auth");
        if (StringUtils.isEmpty(header)){
            builResponse(ResponseEnum.TOKEN_ERROR);
            return null;
            //throw new ShopException(ResponseEnum.TOKEN_HEADLE_NULL);
        }
        //判断头部信息是否完整
        String[] headerArr = header.split("\\.");
        if (headerArr.length!=2){
        builResponse(ResponseEnum.TOKEN_HEADLE_NULL);
            //throw new ShopException(ResponseEnum.TOKEN_ERROR);
            return null;
        }
        //验签
        //用户信息
        String member = headerArr[0];
        //签名
        String signs = headerArr[1];
        //解码
        String memberDecoder = new String(Base64.getDecoder().decode(member),"utf-8");
        String signDecoder = new String(Base64.getDecoder().decode(signs),"utf-8");
        String newSign = Md5Util.sign(memberDecoder,Constants.SECRET);
        if (!newSign.equals(signDecoder)){
            //不相等是给前台一个提示
            builResponse(ResponseEnum.MAIL_QUEUE_ERROR);
            //throw new ShopException(ResponseEnum.MEMEBER_SECRET_ERROR);
        }

        MemberVo memberVo = JSON.parseObject(memberDecoder, MemberVo.class);
        Long id = memberVo.getId();
        //设置JWT的过期时间
        if (!RedisUtil.exist(KeyUtil.builMemberKey(id))){
            builResponse(ResponseEnum.MEMBER_JIHUO_ERROR);
            //throw new ShopException(ResponseEnum.SECRET_ERROR);
        }
        //重新刷新登录时间
        RedisUtil.expire(KeyUtil.builMemberKey(id),5*60);
        //将登录后的用户信息放入到request请求中
        //request.setAttribute(Constants.SECRET,memberVo);
        currentContext.addZuulRequestHeader(Constants.SECRET,URLEncoder.encode(memberDecoder,"utf-8"));
        return null;
    }

    private void builResponse(ResponseEnum responseEnum) {
        //先前台返回一个提示信息
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        response.setContentType("application/json;charset=utf-8");
        currentContext.setSendZuulResponse(false);//拦截了，不会进行路由或者跳转
        //提示JSON信息
        ServerResponse error = ServerResponse.error(responseEnum);
        String s = JSON.toJSONString(error);
        currentContext.setResponseBody(s);
    }
}
