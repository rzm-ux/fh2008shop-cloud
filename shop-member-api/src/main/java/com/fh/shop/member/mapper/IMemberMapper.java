package com.fh.shop.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.member.po.Member;
import org.apache.ibatis.annotations.Param;

public interface IMemberMapper extends BaseMapper<Member> {

    void updateInteger(@Param("id") Long memberId, @Param("integral") Integer integer);

}
