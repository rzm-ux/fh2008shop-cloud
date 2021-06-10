package com.fh.shop.sku.biz;

import com.fh.shop.common.ServerResponse;
import com.fh.shop.sku.vo.StrockVo;

import java.util.List;

public interface ISkuService {
    ServerResponse findSku();


    ServerResponse findsSku(Long id);

}
