package com.dongyimai.cart.service;

import com.dongyimai.entity.Cart;

import java.util.List;

public interface CartService {

    /*添加商品sku到购物车*/
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId,Integer num);

    /*从redis中获取数据*/
    public List<Cart> findCartListFromRedis(String userNam);

    /*往redis中存放数据*/

    public void saveCartListToRedis(String userName,List<Cart> cartList);

    /*合并cookie和redis中的数据*/
    public List<Cart> mergeCookirToRedis(List<Cart> cartList_Cookie,List<Cart> cartList_Redis);
}
