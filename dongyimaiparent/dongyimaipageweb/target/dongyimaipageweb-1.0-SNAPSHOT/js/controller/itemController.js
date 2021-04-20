app.controller('itemController',function ($scope,$http){

    $scope.selectspec={};
    $scope.item={};
    /*购物车数量*/
    $scope.num=1;
    $scope.addCateNum=function (num){
        $scope.num=$scope.num+num;
        if (num<1){
            $scope.num=1;
        }
    }

    /*用户对规格数据的选择*/
    $scope.addspec=function(key,value){
        $scope.selectspec[key]=value;
        showSpec();
        console.info($scope.item)


    }
    /*/!*判断是否选中*!/*/
    $scope.isSelectes=function (key,value){
        if ($scope.selectspec[key] == value){
            return true;
        }else {
            return  false;
        }
        console.info($scope.selectspec[key])
    }

    /*/!*显示默认选中的规格*!/*/
    $scope.showDefau=function (){
        $scope.item=itemList[0];
        $scope.selectspec=JSON.parse(JSON.stringify($scope.item.spec));
    }


    /*判断两个对象是否相等*/
    machObject=function (obj1,obj2){
        for (var key1 in obj1){
            if (obj1[key1] != obj2[key1]){
                return false;
            }
        }
        return true;
    }

    /**/
    showSpec=function (){
        console.info($scope.selectspec)
        for (var i =0;i<itemList.length;i++){
            if (machObject(itemList[i].spec,$scope.selectspec)){
                $scope.item=itemList[i];
                return;
            }
        }
    }

    /*添加购物车*/
    $scope.addCat=function (){
        $http.get("http://localhost:9109/cart/addCart.do?itemId="+$scope.item.id+"&num="+$scope.num,{'withCredentials':true}).success(
            function (response){
                if (response.success){
                    location.href="http://localhost:9109/cart.html";
                }else{
                    alert(response.message)
                }
            }
        )
    }
})