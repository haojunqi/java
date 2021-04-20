app.controller("seckillcontroller",function ($scope,$location,$interval,seckillservice){

    /*获取所有的秒杀商品*/
    $scope.getAll=function (){
        seckillservice.getAllGoodes().success(
            function (response){
                $scope.seckillGoodesList=response;
                console.info($scope.seckillGoodesList)
            }
        )
    }

    /*获取一个商品的详细信息页面*/
    $scope.getOne=function (){
        seckillservice.getOnegoods($location.search()['id']).success(
            function (response){
                $scope.onegoods=response;
                /*距离秒杀开始的时间*/
                var start_time=(new Date($scope.onegoods.startTime).getTime()-new Date())/1000;
                $interval(function (){
                    if (start_time >0){
                        start_time-=1;
                        $scope.starts=secondsToTime(start_time)
                    }
                },1000)
                /*倒计时方法*/
                var seconds=(new Date($scope.onegoods.endTime).getTime()-new Date().getTime())/1000;
                $interval(function (){
                    if (seconds >0){
                        seconds -= 1;
                        $scope.timestr=secondsToTime(seconds);
                    }
                },1000)
            }
        )
    }

    /*时间格式化工具*/
    secondsToTime=function (seconds){
        var str ="";
        /*天数*/
        var day =Math.floor(seconds/(60*60*24));
        /*不满足一天剩下的小时数*/
        var hours=Math.floor(seconds%(60*60*24)/(60*60));
        /*分钟*/
        var min=Math.floor(seconds % (60*60)/60 );
        var s = Math.floor(seconds % 60);
        if (day >0){
            str += day+"天"
        }
        str+=hours+":"+min+":"+s+"";
        return str;
    }

    /*页面跳转*/
    $scope.showgoods=function (id){
        location.href='seckill-item.html#?id='+id;
    }

    /**/
    $scope.submitOrder=function (){
        seckillservice.submit($scope.onegoods.id).success(
            function (response){
                if (response.success){
                    alert(response.message)
                    location.href='pay.html';
                }else {
                    if (response.message=="nologin"){
                        location.href="login.html";
                    }
                    alert(response.message);
                }
            }
        )


    }
})