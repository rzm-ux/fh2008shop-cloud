package com.fh.shop.api.goods.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StrockVo implements Serializable {


    public String skuName;

    private String cateName;

    private BigDecimal pirce;

    private int     stock;

    private String      brandName;

}
