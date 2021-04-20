app.controller("cartController",function ($scope,cartService){

    $scope.address={};
    $scope.order={paymentTypr:'1'}

    $scope.selectpayType=function (type){
        $scope.order.paymentTypr=type
    }
    /*获取购物车所有的数据*/
    $scope.findAllCartList=function (){
        cartService.findAllCartList().success(
            function (response){
                $scope.cartList=response;
                console.info($scope.cartList)
               $scope.totals=cartService.sum($scope.cartList)
            }
        )
    }


    /*获取原来的商品数量，增加或减少之后重新添加到购物车*/
    $scope.addNumCartList=function (itemId,num){
        cartService.addNumToCartList(itemId,num).success(
            function (response){
                if (response.success){
                    $scope.findAllCartList()
                }else {
                    alert(response.message)
                }
            }
        )
    }

    /*获取用户名*/
    $scope.getName=function (){
        cartService.getUserName().success(
            function (response){
                $scope.userName=response.userName
            }
        )
    }


    /*获取地址信息*/
    $scope.findListAddress=function (){
        cartService.findListByUserId().success(
            function (response){
                $scope.adderssList=response;
               if ($scope.adderssList[0].isDefault==1){
                   $scope.address=$scope.adderssList[0]
               }
            }
        )
    }

    /*当用户选择地址*/
    $scope.selectAddress=function (address){
        $scope.address=address;

    }


    /*判断用户地址是否被选择*/
    $scope.isselected=function (address){
        if (address == $scope.address){
            return true;
        }else {
            return false
        }
    }


    /*保存订单信息*/
    $scope.addOrder=function (){
        alert("执行")
        $scope.order.receiverAreaName=$scope.address.constructor.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;
        cartService.submitOrder($scope.order).success(
            function (response){
                /*如果选择的是支付宝支付就跳转到付款页面*/
                if (response.success){
                    if ($scope.order.paymentTypr=='1'){
                        location.href="pay.html";
                    }else {
                        /*如果是货到付款就直接跳转到成功页面*/
                        location.href="paysuccess.html"
                    }
                }else {
                    alert(response.message);
                }
            }
        )
    }
})