app.service("payService",function($http){
    this.createQrCode = function(){
        return $http.get("pay/queryCode.do");
    }

    this.queryStatus = function(out_trade_no){
        return $http.get("pay/queryStatus.do?out_trade_no=" + out_trade_no);
    }
})