package com.fh.shop.sku.controller;

import com.fh.shop.common.ServerResponse;
import com.fh.shop.sku.biz.ISkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Slf4j
public class SkuController {

    @Value("${server.port}")
    private String port;
    @Resource(name = "ISkuService")
    private ISkuService iSkuService;

    @GetMapping("/skus")
    private ServerResponse findSku(){
        log.info("端口号信息:{}"+port);
        return iSkuService.findSku();
    }
    @GetMapping("/skus/findsSku")
    public ServerResponse findsSku(@RequestParam("id") Long id){
        return iSkuService.findsSku(id);
    }
}
