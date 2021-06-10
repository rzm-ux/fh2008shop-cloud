package com.fh.shop.sku.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.sku.mapper.ISkuMapper;
import com.fh.shop.sku.po.Sku;
import com.fh.shop.sku.vo.SkuVo;
import com.fh.shop.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service("ISkuService")
@Transactional(rollbackFor = Exception.class)
public class ISkuServiceImpl implements ISkuService {
    @Autowired
    private ISkuMapper iSkuMapper;
    @Override
    public ServerResponse   findSku() {
        //String skuList1 = RedisUtil.get("skuList");
        //if (StringUtils.isEmpty(skuList1)){
        List<Sku>skuList= iSkuMapper.findSku();
        List<SkuVo> collect = skuList.stream().map(x -> {
            SkuVo skuVo = new SkuVo();
            skuVo.setId(x.getId());
            skuVo.setSkuName(x.getSkuName());
            skuVo.setImage(x.getImage());
            skuVo.setPrice(x.getPrice());
            skuVo.setStock(x.getStock());
            return skuVo;
        }).collect(Collectors.toList());
            String s = JSON.toJSONString(collect);
            //RedisUtil.setex("skuList",s,20);
            return ServerResponse.success(skuList);
        //}
//        else {
//            List<Sku> skuList = JSON.parseArray(skuList1, Sku.class);
//            return ServerResponse.success(skuList);
//        }
    }

    @Override
    public ServerResponse findsSku(Long id) {
        Sku sku = iSkuMapper.selectById(id);
        return ServerResponse.success(sku);
    }


}
