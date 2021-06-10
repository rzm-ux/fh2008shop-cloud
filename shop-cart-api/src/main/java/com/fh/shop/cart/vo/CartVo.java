package com.fh.shop.cart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CartVo implements Serializable {

    private List<CartItemVo> cartItemVoList=new ArrayList<>();

    private Long            totalcount;

    private String          totalPrice;

}
