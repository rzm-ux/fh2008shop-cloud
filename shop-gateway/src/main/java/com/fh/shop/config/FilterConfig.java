package com.fh.shop.config;

import com.fh.shop.filter.Filter;
import com.fh.shop.filter.Filter1;
import com.fh.shop.filter.Filter3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class FilterConfig {

    @Bean
    public Filter filter(){
        return new Filter();
    }

    @Bean
    public Filter1 filter1(){
        return new Filter1();
    }

    @Bean
    public Filter3 filter3(){
        return new Filter3();
    }
}
