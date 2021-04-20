package com.dongyimai.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.cart.service.CartService;
import com.dongyimai.entity.Cart;
import com.dongyimai.mapper.TbItemMapper;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.pojo.TbOrder;
import com.dongyimai.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(timeout = 6000000)
public class CartServiceImpl implements CartService {

   @Autowired
    private  TbItemMapper tbItemMapper;

   @Autowired
   private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        /*先通过itemid获取sku的数据*/
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        /*如果tbitem数据为空，则代表没有商品*/
        if (tbItem == null){
            throw  new RuntimeException("商品已下架");
        }
        if (!tbItem.getStatus().equals("1")){
            throw new RuntimeException("商品状态异常");
        }
        String sellerId = tbItem.getSellerId();
        /*获取商品对应的商家*/
        Cart cart = searchCartBySellerId(cartList, sellerId);
        /*判断返回的cart中是否存在数据，*/
        /*如果购物车中不存在该商家，那么就在购物车中新建这个商家信息*/
        if (cart==null){
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
           List<TbOrderItem> items =new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(tbItem, num);
            items.add(orderItem);
            cart.setOrderList(items);
            cartList.add(cart);
        }else{
            /*如果购物车中存在商家信息,那么就判断商家的列表中是否存在该商品*/
            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderList(), itemId);
            /*如果不存在该商品，那么就创建一个新的*/
            if (tbOrderItem==null){
                TbOrderItem orderItem = createOrderItem(tbItem, num);
                cart.getOrderList().add(orderItem);
            }else{
                tbOrderItem.setNum(tbOrderItem.getNum()+num);
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum()*tbOrderItem.getPrice().doubleValue()));

                /*在添加的购物车集合中，如果商品的数量等于零，那么就把该商品信息删除.*/
                if (tbOrderItem.getNum()==0){
                    cart.getOrderList().remove(tbOrderItem);
                }

                /*如果购物车中的商品集合的大小等于零，那么就把商家信息删除*/
                if (cart.getOrderList().size()==0){
                    cart.getOrderList().remove(cart);
                }

            }
        }

        return cartList;
    }



    /*判断购物车列表中是否有商家id*/
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        /*遍历出集合中想要添加到购物车的cart对象。获取到里面的商家id，
        * 与查询出来的做对比，如果相同证明购物车中存在该商家*/
        for (Cart cart: cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
    /*创建orderitem*/
    /*把查询到的商品对象， 转换成orderitem对象*/
    private TbOrderItem createOrderItem(TbItem tbItem,Integer num){
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setItemId(tbItem.getId());
        tbOrderItem.setNum(num);
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setPrice(tbItem.getPrice());
        tbOrderItem.setSellerId(tbItem.getSellerId());
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*num));
        return tbOrderItem;
    }

    /*判断商家中是否存在该商品*/
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> tbOrderItemList,Long itemId){
        for (TbOrderItem tbOrderItem :tbOrderItemList) {
            if (tbOrderItem.getItemId().equals(itemId)){
                return tbOrderItem;
            }
        }
        return null;
    }



    /*在Redis中获取数据*/
    @Override
    public List<Cart> findCartListFromRedis(String userNam) {
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps(userNam).get(userNam);
        if (cartList==null){
            cartList = new ArrayList<>();
            return cartList;
        }
        return cartList;
    }

    /*往redis中存放数据*/
    @Override
    public void saveCartListToRedis(String userName, List<Cart> cartList) {
        redisTemplate.boundHashOps(userName).put(userName,cartList);

    }


    /*合并cookies和redis的数据*/
    @Override
    public List<Cart> mergeCookirToRedis(List<Cart> cartList_Cookie, List<Cart> cartList_Redis) {
        for (Cart cart : cartList_Cookie){
            for (TbOrderItem orderItem : cart.getOrderList()){
                cartList_Redis= addGoodsToCartList(cartList_Redis, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList_Redis;
    }
}
