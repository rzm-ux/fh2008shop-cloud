package com.fh.shop.cate.vo;


import com.fh.shop.cate.po.Cate;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CateVo implements Serializable {

    private Cate cate=new Cate();

    //private List<Type>typeList=new ArrayList<>();


}
