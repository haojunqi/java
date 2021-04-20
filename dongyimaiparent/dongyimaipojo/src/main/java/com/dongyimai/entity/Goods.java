package com.dongyimai.entity;

import com.dongyimai.pojo.TbGoods;
import com.dongyimai.pojo.TbGoodsDesc;
import com.dongyimai.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

public class Goods implements Serializable {

    private TbGoods tbGoods;
    private TbGoodsDesc tbGoodsDesc;
    private List<TbItem> tbItemList;

    public Goods() {
    }

    public Goods(TbGoods tbGoods, TbGoodsDesc tbGoodsDesc, List<TbItem> tbItemList) {
        this.tbGoods = tbGoods;
        this.tbGoodsDesc = tbGoodsDesc;
        this.tbItemList = tbItemList;
    }

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getTbGoodsDesc() {
        return tbGoodsDesc;
    }

    public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
        this.tbGoodsDesc = tbGoodsDesc;
    }

    public List<TbItem> getTbItemList() {
        return tbItemList;
    }

    public void setTbItemList(List<TbItem> tbItemList) {
        this.tbItemList = tbItemList;
    }
}
