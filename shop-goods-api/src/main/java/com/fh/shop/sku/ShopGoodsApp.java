package com.fh.shop.sku;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.fh.shop.sku.mapper")
@EnableDiscoveryClient
public class ShopGoodsApp {
    public static void main(String[]args){
        SpringApplication.run(ShopGoodsApp.class,args);
    }


}
