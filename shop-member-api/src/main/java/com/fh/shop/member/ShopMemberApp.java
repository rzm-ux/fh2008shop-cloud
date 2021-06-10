package com.fh.shop.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.fh.shop.member.mapper")
@ComponentScan(basePackages={"com.fh.shop.api.swaage"})//引入swagger2
public class ShopMemberApp {

    public static void main(String[] args) {
        SpringApplication.run(ShopMemberApp.class,args);
    }
}
