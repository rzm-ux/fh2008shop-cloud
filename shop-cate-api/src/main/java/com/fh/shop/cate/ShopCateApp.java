package com.fh.shop.cate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.fh.shop.cate.mapper")
@EnableDiscoveryClient
public class ShopCateApp {

    public static void main(String[] args){
        SpringApplication.run(ShopCateApp.class,args);
    }


}
