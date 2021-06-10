package com.fh.shop.member.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.common.Constants;
import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.member.mapper.IMemberMapper;
import com.fh.shop.member.po.Member;
import com.fh.shop.member.vo.MemberVo;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("IMemberService")
@Transactional(rollbackFor = Exception.class)
public class IMemberServiceImpl implements IMemberService {
    @Autowired
    private IMemberMapper iMemberMapper;


    @Override
    public ServerResponse login(String memberName, String password) {
        //验证非空
        if (StringUtils.isEmpty(memberName) || StringUtils.isEmpty(password)) {

            return ServerResponse.error(ResponseEnum.MEMBER_VIP_NULL);
        }
        //判断会员是否存在
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("memberName", memberName);
        Member member = iMemberMapper.selectOne(wrapper);
        if (member==null){
            return ServerResponse.error(ResponseEnum.MEMBER_VIP_NULL);
        }
        //验证密码是否正确
        if (!member.getPassword().equals(Md5Util.md5(password))) {
            return ServerResponse.error(ResponseEnum.MEMEBER_PASSWORD_ERROR);
        }
        Integer start = member.getStart();
        if (start!=Constants.STATUS){
            return ServerResponse.error(ResponseEnum.MEMBER_JIHUO_NULL);
        }
        if (member.getStart()!=1){
            String mail = member.getMail();
            String uuid = UUID.randomUUID().toString();
            RedisUtil.setex(KeyUtil.builactivation(uuid),member.getId()+"",5*60);
            Map<String, String>result=new HashMap<>();
            result.put("mail",mail);
            result.put("uuid",uuid);
            return ServerResponse.error(ResponseEnum.MEMBER_JIHUO_ERROR,result);
        }
        //生成签名
        //向前台响应数据
        MemberVo memberVo = new MemberVo();
        memberVo.setId(member.getId());
        memberVo.setMemberName(member.getMemberName());
        memberVo.setNickName(member.getNickName());
        Long id = member.getId();
        String memberJSON = JSON.toJSONString(memberVo);
        String sign = Md5Util.sign(memberJSON, Constants.SECRET);
        //将用户信息进行base64编码
        String memberBase = Base64.getEncoder().encodeToString(memberJSON.getBytes());
        //将签名信息进行base64编码
        String sercrtBase = Base64.getEncoder().encodeToString(sign.getBytes());
        //将用户信息返回给前台【但不包括密码】
        //将用户信息和签名通过,分割成一个字符串【x,y】
        //设置JWT的过期时间
        RedisUtil.setex(KeyUtil.builMemberKey(id), "", 2 * 320);
        return ServerResponse.success(memberBase + "." + sercrtBase);
    }

}

