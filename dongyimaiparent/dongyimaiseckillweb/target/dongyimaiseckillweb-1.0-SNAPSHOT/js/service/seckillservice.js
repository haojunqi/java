app.service("seckillservice",function ($http){
    this.getAllGoodes=function (){
        return $http.get("/seckill/getAll.do");
    }
    this.getOnegoods=function (id){
        return $http.get("/seckill/getOne.do?id="+id)
    }
    this.submit=function (seckillGoodsId){
        return $http.get("/seckill/submigOrder.do?seckillGoodsId="+seckillGoodsId)
    }
})