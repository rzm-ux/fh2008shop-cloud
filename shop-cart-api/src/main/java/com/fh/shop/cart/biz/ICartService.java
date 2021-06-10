package com.fh.shop.cart.biz;

import com.fh.shop.common.ServerResponse;

public interface ICartService {
    ServerResponse addCartItem(Long memberId, Long skuId, Long count);

    ServerResponse findCart(Long memberId);

    ServerResponse deleteCart(Long id,Long memberId);

    ServerResponse deleteCartBatch(String ids, Long memberId);
}
