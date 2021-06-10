package com.fh.shop.cart.controller;

import com.fh.shop.api.BaseController;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.cart.biz.ICartService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/carts")
@Api(tags = "购物车接口")
public class CartController extends BaseController {

    @Resource(name = "ICartService")
    private ICartService iCartService;

    @Autowired
    private HttpServletRequest request;

    //添加购物车

    @PostMapping("/addCartItem")
    @ApiOperation("添加购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId",value = "商品信息",dataType = "java.lang.long",required = true),
            @ApiImplicitParam(name = "count",value = "数量",dataType = "java.lang.long",required = true),
            @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    })
    public ServerResponse addCartItem(Long skuId,Long count){
       // MemberVo memberVo = (MemberVo) request.getAttribute(Constants.SECRET);
        MemberVo memberVo = buildmemberVo(request);
        Long memberId = memberVo.getId();
        return iCartService.addCartItem(memberId,skuId,count);
    }

    @GetMapping("/queryCart")
    @ApiOperation("购物车查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    })
    public ServerResponse findCart(){
        //MemberVo memberVo = (MemberVo) request.getAttribute(Constants.SECRET);
        MemberVo memberVo = buildmemberVo(request);
        Long memberId = memberVo.getId();
        return iCartService.findCart(memberId);
    }
    @PostMapping("/deleteCart")
    @ApiOperation("删除购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    })
    public ServerResponse deleteCart(Long id){
        //MemberVo memberVo = (MemberVo) request.getAttribute(Constants.SECRET);
        MemberVo memberVo = buildmemberVo(request);
        Long memberId = memberVo.getId();

        return iCartService.deleteCart(id,memberId);
    }
    @PostMapping("/deleteCartBatch")
    @ApiOperation("删除批量删除购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    })
    public ServerResponse deleteCartBatch(String ids){
       // MemberVo memberVo = (MemberVo) request.getAttribute(Constants.SECRET);
        MemberVo memberVo = buildmemberVo(request);
        Long memberId = memberVo.getId();

        return iCartService.deleteCartBatch(ids,memberId);
    }
}
