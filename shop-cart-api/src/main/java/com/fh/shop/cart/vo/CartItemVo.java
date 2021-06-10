package com.fh.shop.cart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItemVo implements Serializable {

    private String skuName;

    private String skuImage;

    private String price;

    private Long  count;

    private Long skuId;

    private String suoPrice;


}
