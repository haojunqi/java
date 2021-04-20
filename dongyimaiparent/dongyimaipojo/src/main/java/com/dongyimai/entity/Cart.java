package com.dongyimai.entity;

import com.dongyimai.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    private String sellerId;
    private String sellerName;
    private List<TbOrderItem> orderList;

    public Cart() {
    }

    public Cart(String sellerId, String sellerName, List<TbOrderItem> orderList) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.orderList = orderList;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<TbOrderItem> orderList) {
        this.orderList = orderList;
    }
}
