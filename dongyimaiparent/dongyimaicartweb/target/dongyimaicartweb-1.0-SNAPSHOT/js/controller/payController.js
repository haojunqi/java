app.controller("payController",function($scope,$location,payService){

    // 生成二维码
    $scope.createQrCode = function(){
        payService.createQrCode().success(
            function(response){
                $scope.out_trade_no = response.out_trade_no;
                $scope.total_amount = response.total_amount;
                var qr = new QRious({
                    level: 'H',
                    size: 250,
                    value: response.qrcode,
                    element:document.getElementById("qrcode")
                });
                $scope.queryStatus();
            }
        )
    }

    // 查询支付状态
    $scope.queryStatus = function(){
        payService.queryStatus($scope.out_trade_no).success(
            function(response){
                if(response.success){
                    // 支付成功
                    location.href="paysuccess.html#?money=" + $scope.total_amount;
                }else{
                    // 支付失败
                    location.href="payfail.html";
                }
            }
        )
    }

    // 接收传递的参数
    $scope.showMoney = function(){
        return $location.search()['money'];
    }

})