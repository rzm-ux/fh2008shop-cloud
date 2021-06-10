package com.fh.shop.cart.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.common.Constants;
import com.fh.shop.api.common.KeyUtil;
import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.cart.vo.CartItemVo;
import com.fh.shop.cart.vo.CartVo;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.goods.IGoodsServer;
import com.fh.shop.util.BigDecimalUtils;
import com.fh.shop.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Service("ICartService")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ICartServiceImpl implements ICartService {

//    @Autowired
//    private ISkuMapper iSkuMapper;

    @Autowired
    private IGoodsServer iGoodsServer;

    @Value("${stock.count.limt}")
    private int countLimit;

    @Override
    public ServerResponse addCartItem(Long memberId, Long skuId, Long count) {

        if (count>countLimit){
            return ServerResponse.error(ResponseEnum.CART_SKU_COUNT);
        }
        //商品是否存在
        ServerResponse<Sku> skuServerResponse = iGoodsServer.findsSku(skuId);
        log.info("=========={}"+skuServerResponse);
        Sku sku = skuServerResponse.getData();//iSkuMapper.selectById(skuId);
        if (sku==null){

            return ServerResponse.error(ResponseEnum.CART_SKU_NULL);
        }
        //商品是否上架
        if (sku.getIsup().equals(Constants.ISUP_UP)){
            return ServerResponse.error(ResponseEnum.CART_SKU_ISUP);
        }
        //当商品的库存小于购买量时
        if(sku.getStock()<count){
            return ServerResponse.error(ResponseEnum.CART_SKU_STOCK);
        }
        //会员是否有购物车
        String key = KeyUtil.builCartSku(memberId);
        String cartCount = KeyUtil.builCartCount();
        String builCart = KeyUtil.builCart(memberId);

        String cartJson = RedisUtil.hget(builCart,key);
        CartVo cartVo = new CartVo();
        Map<String, String> map = new HashMap<>();
        CartItemVo cartItemVo = new CartItemVo();
        if (StringUtils.isEmpty(cartJson)){
            //如果购物车没有，就创建一个购物车，直接把商品放进去\
            CartSku(count, sku, cartVo, cartItemVo);
            cartVo.setTotalcount(count);
            cartVo.setTotalPrice(cartItemVo.getSuoPrice());
            //RedisUtil.set(key,JSON.toJSONString(cartVo));
            map.put(key,JSON.toJSONString(cartVo));
            map.put(cartCount,count+"");
            RedisUtil.hmset(builCart,map);
        }else {
            //如果有购物车
            CartVo cartVo1 = JSON.parseObject(cartJson, CartVo.class);
            List<CartItemVo> cartItemVoList = cartVo1.getCartItemVoList();
            Optional<CartItemVo> first = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
            if (first.isPresent()){
                //购物车有这款商品，找到这款商品，更新商品的数量，小计
                CartItemVo cartItemVo1 = first.get();
                long count1 = cartItemVo1.getCount() + count;
                if (count1>countLimit){
                    return ServerResponse.error();
                }
                if (count1<=0){
                    //从购物车中删除出数据
                    cartItemVoList.removeIf(x->x.getSkuId().longValue()==cartItemVo1.getSkuId().longValue());
                    if (cartItemVoList.size()==0){
                        RedisUtil.delete(key);
                        return ServerResponse.success();
                    }
                    //更新数据
                    updateCart(key, cartVo1,memberId);
                    return ServerResponse.success();
                }
                cartItemVo1.setCount(count1);
                BigDecimal bigDecimal = new BigDecimal(cartItemVo1.getSuoPrice());
                String subPrice = bigDecimal.add(BigDecimalUtils.mul(cartItemVo1.getPrice(), count + "")).toString();
                cartItemVo1.setSuoPrice(subPrice);
                long totalCount=0;
                BigDecimal totalPrice = new BigDecimal(0);
                for (CartItemVo itemVo : cartItemVoList) {
                    totalCount+=itemVo.getCount();
                    totalPrice= totalPrice.add(new BigDecimal(itemVo.getSuoPrice()));
                }
                cartVo1.setTotalcount(totalCount);
                cartVo1.setTotalPrice(totalPrice.toString());
                //RedisUtil.set(key,JSON.toJSONString(cartVo1));
                //更新购物车（redis中的购物车）
                map.put(key,JSON.toJSONString(cartVo1));
                map.put(cartCount,totalCount+"");
                RedisUtil.hmset(builCart,map);
            }else {
                //但购物车中没有这件商品，及直接把商品放进去
                CartItemVo cartItemVo1 = new CartItemVo();

                cartItemVo1.setCount(count);
                String price = sku.getPrice().toString();
                cartItemVo1.setPrice(price);
                cartItemVo1.setSkuImage(sku.getImage());
                cartItemVo1.setSkuName(sku.getSkuName());
                cartItemVo1.setSkuId(sku.getId());
                BigDecimal suoPrice = BigDecimalUtils.mul(price, count + "");
                cartItemVo1.setSuoPrice(suoPrice.toString());
                cartVo1.getCartItemVoList().add(cartItemVo1);
                updateCart(key, cartVo1,memberId);
            }
        }
        return ServerResponse.success();
    }

    private void updateCart(String key, CartVo cartVo1,Long memberId) {
        Map<String,String>map=new HashMap<>();
        String cartCount = KeyUtil.builCartCount();
        String builCart = KeyUtil.builCart(memberId);
        List<CartItemVo> cartItemVos = cartVo1.getCartItemVoList();
        long totalCount=0;
        BigDecimal totalPrice = new BigDecimal(0);
        for (CartItemVo itemVo : cartItemVos) {
            totalCount+=itemVo.getCount();
            totalPrice= totalPrice.add(new BigDecimal(itemVo.getSuoPrice()));
        }
        cartVo1.setTotalcount(totalCount);
        cartVo1.setTotalPrice(totalPrice.toString());
        //更新购物车（redis中的购物车）
        //RedisUtil.set(key,JSON.toJSONString(cartVo1));
        map.put(key,JSON.toJSONString(cartVo1));
        map.put(cartCount,totalCount+"");
        RedisUtil.hmset(builCart,map);
    }
    //提取全局的方法主要功能：接受前台的数据给cartVo进行的List进行赋值
    private void CartSku(Long count, Sku sku, CartVo cartVo, CartItemVo cartItemVo) {
        cartItemVo.setCount(count);
        String price = sku.getPrice().toString();
        cartItemVo.setPrice(price);
        cartItemVo.setSkuImage(sku.getImage());
        cartItemVo.setSkuName(sku.getSkuName());
        cartItemVo.setSkuId(sku.getId());
        BigDecimal suoPrice = BigDecimalUtils.mul(price, count + "");
        cartItemVo.setSuoPrice(suoPrice.toString());
        cartVo.getCartItemVoList().add(cartItemVo);
    }


    @Override
    public ServerResponse findCart(Long memberId) {
        //查询购物车的数量，将数据返回给前台
        String cartId = RedisUtil.hget(KeyUtil.builCart(memberId),KeyUtil.builCartSku(memberId));
        CartVo cartVo = JSON.parseObject(cartId, CartVo.class);
        return ServerResponse.success(cartVo);
    }

    @Override
    public ServerResponse deleteCart(Long id,Long memberId) {
        //删除购物车的数据

        String hget = RedisUtil.hget(KeyUtil.builCart(memberId), KeyUtil.builCartSku(memberId));
        String key= KeyUtil.builCartSku(memberId);
        String builCart = KeyUtil.builCart(memberId);
        String cartCount = KeyUtil.builCartCount();

        CartVo cartVo = JSON.parseObject(hget, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        cartItemVoList.removeIf(x->x.getSkuId().longValue()==id);
        if (cartItemVoList.size()==0){
            RedisUtil.delete(builCart);
            return ServerResponse.success();
        }
        long totalCount=0;
        BigDecimal totalPrice = new BigDecimal(0);
        for (CartItemVo itemVo : cartItemVoList) {
            totalCount+=itemVo.getCount();
            totalPrice= totalPrice.add(new BigDecimal(itemVo.getSuoPrice()));
        }
        cartVo.setTotalcount(totalCount);
        cartVo.setTotalPrice(totalPrice.toString());


        Map<String,String>map=new HashMap<>();
        map.put(key,JSON.toJSONString(cartVo));
        map.put(cartCount,totalCount+"");
        RedisUtil.hmset(builCart,map);

        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteCartBatch(String ids, Long memberId) {
        if (StringUtils.isEmpty(ids)){
            return ServerResponse.error(ResponseEnum.CART_DELETE_NULL);
        }
        String hget = RedisUtil.hget(KeyUtil.builCart(memberId), KeyUtil.builCartSku(memberId));
        String key= KeyUtil.builCartSku(memberId);
        String builCart = KeyUtil.builCart(memberId);
        String cartCount = KeyUtil.builCartCount();

        CartVo cartVo = JSON.parseObject(hget, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        Arrays.stream(ids.split(",")).forEach(x->cartItemVoList.removeIf(y->y.getSkuId().longValue()==Long.parseLong(x)));
        if (cartItemVoList.size()==0){
            RedisUtil.delete(builCart);
            return ServerResponse.success();
        }
        long totalCount=0;
        BigDecimal totalPrice = new BigDecimal(0);
        for (CartItemVo itemVo : cartItemVoList) {
            totalCount+=itemVo.getCount();
            totalPrice= totalPrice.add(new BigDecimal(itemVo.getSuoPrice()));
        }
        cartVo.setTotalcount(totalCount);
        cartVo.setTotalPrice(totalPrice.toString());


        Map<String,String>map=new HashMap<>();
        map.put(key,JSON.toJSONString(cartVo));
        map.put(cartCount,totalCount+"");
        RedisUtil.hmset(builCart,map);

        return null;
    }
}
