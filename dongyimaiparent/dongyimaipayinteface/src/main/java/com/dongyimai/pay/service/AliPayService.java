package com.dongyimai.pay.service;

import java.util.Map;

public interface AliPayService {

    /*生成支付宝二维码支付*/
    public Map creatNative(String out_trade_no, String total_amount);

    public Map queryPayStatus(String out_trade_no);
}
