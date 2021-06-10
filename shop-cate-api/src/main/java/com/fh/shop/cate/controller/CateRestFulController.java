package com.fh.shop.cate.controller;



import com.fh.shop.cate.biz.ICateService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(tags = "分类展示")
public class CateRestFulController {

    @Resource(name = "ICateService")
    private ICateService iCateService;

    @ApiOperation("查询")
    @RequestMapping(value = "/cates",method = RequestMethod.GET)
    public ServerResponse cateList(){

        return iCateService.cateList();
    }

}
