package com.fh.shop.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

@Slf4j
public class Filter extends ZuulFilter {


    /**
     * Type:表示过滤器类型，
     * 有下面几种
     *pre 所有客户端在访问真正的微服务前执行
     *post 所有客户端在访问真正的微服务后执行
     *route
     *error
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
        //return "pre" 关键字不能改
    }
    /**
     * Order:
     * 数值越小代表这个优先级越高(前提是在同一过滤器中才生效)
     *
     *
     */
    @Override
    public int filterOrder() {
        return 7;
    }
    /**
     * shouldFilter:
     * 表示当前过滤器是否生效
     *  true:生效
     *  false:不生效
     */
    @Override
    public boolean shouldFilter() {
        return false;
    }
    /**
     * run:
     * 处理所有的逻辑业务
     *  永远返回null
     *
     */
    @Override
    public Object run() throws ZuulException {
        log.info("==========");
        return null;
    }
}
