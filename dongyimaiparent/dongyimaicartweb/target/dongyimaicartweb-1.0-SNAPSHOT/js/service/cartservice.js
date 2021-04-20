app.service("cartService",function ($http){
    this.findAllCartList=function (){
        return $http.get("../cart/findCartList.do")
    }

    /*更改购物车的商品数量，添加到购物车*/
    this.addNumToCartList=function (itemId,num){
        return $http.get("../cart/addCart.do?itemId="+itemId+"&num="+num);
    }
        /*获取购物车中商品的总数量，和商品的总价格*/

    this.sum=function (cartList){
        var total={totalNum:0,totalMoney:0.00}
        for (var i = 0; i < cartList.length; i++) {
            var cart=cartList[i];
            console.info(cart)
            for (var j = 0; j < cart.orderList.length; j++) {
                var orderItem = cart.orderList[j]
               total.totalNum+=orderItem.num;
                total.totalMoney+=orderItem.totalFee
            }
        }
        return total;
    }

    /*获取登录名*/
    this.getUserName=function (){
        return $http.get("../cart/getUserName.do")
    }


    /*获取地址信息*/
    this.findListByUserId=function (){
        return $http.get("/address/findListByUserId.do");
    }

    /*保存订单信息*/
    this.submitOrder=function (order){
        return $http.post("order/add.do",order)
    }
})