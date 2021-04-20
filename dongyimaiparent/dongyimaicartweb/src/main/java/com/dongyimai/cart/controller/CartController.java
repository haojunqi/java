package com.dongyimai.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dongyimai.cart.service.CartService;
import com.dongyimai.entity.Cart;
import com.dongyimai.entity.Result;
import com.offcn.utils.CookieUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("cart")
public class CartController {

    @Reference
    private CartService cartService;

    /*添加购物车*/
    @RequestMapping("addCart")
    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    public Result addCart( HttpServletRequest request, HttpServletResponse response,Long itemId,Integer num){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cartList = findAllCartList(request,response);
        cartList = cartService.addGoodsToCartList(cartList,itemId,num);
        try {
            if ("anonymousUser".equals(name)){
                String Cart_str = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",Cart_str,24*60*60,"utf-8");

            }else {
                cartService.saveCartListToRedis(name,cartList);
            }
            return  new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    /*返回购物车列表*/
    @RequestMapping("/findCartList")
    public List<Cart> findAllCartList(HttpServletRequest request,HttpServletResponse response){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cartList_cookie=null;
            String cart_str = CookieUtil.getCookieValue(request, "cartList","utf-8");
            if (cart_str==null){
                cart_str="[]";
            }
            cartList_cookie = JSON.parseArray(cart_str, Cart.class);
        if ("anonymousUser".equals(userName)){
            return cartList_cookie;
        }else {
            /*从redis中获取数据*/
            List<Cart> cartList_redis = cartService.findCartListFromRedis(userName);
            if (cartList_cookie!=null && cartList_cookie.size() >0 ){
              cartList_redis = cartService.mergeCookirToRedis(cartList_cookie, cartList_redis);
                /*删除cookie原来的数据*/
                CookieUtil.deleteCookie(request,response,"cartList");
                /*重新添加到redis*/
                cartService.saveCartListToRedis(userName,cartList_redis);
            }
            return cartList_redis;
        }

    }

    /*获取登录名*/
    @RequestMapping("getUserName")
    public Map getUserName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap<String, String> map = new HashMap<>();
        map.put("userName",name);
        return map;
    }
}
