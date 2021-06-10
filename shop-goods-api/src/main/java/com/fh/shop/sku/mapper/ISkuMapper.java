package com.fh.shop.sku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.sku.po.Sku;
import com.fh.shop.sku.vo.StrockVo;

import java.util.List;

public interface ISkuMapper extends BaseMapper<Sku> {

    List<Sku> findSku();

    List<StrockVo> findSkuList(int stockLimt);

}
