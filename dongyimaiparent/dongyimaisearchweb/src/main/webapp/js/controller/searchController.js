/*过滤器，信任html*/
app.filter("trustHTML",['$sce',function ($sce){
    return function (data){
        return $sce.trustAsHtml(data);
    }
}])


app.controller("searchController",function ($scope,$location,searchService){

    $scope.searchMap={'keywords':'',"brand":'',"category":'',"spec":{},'price':"",'pageNo':1,'pageSize':10,'sortField':'','sort':''};
    //定义一个用来添加属性的方法
    $scope.addFilterSearch=function (key,value){
        if ("brand"==key || key=="category"  || key=="price"){
            $scope.searchMap[key]=value;
            console.info($scope.searchMap)
        }else {
            $scope.searchMap.spec[key]=value
        }
        $scope.search();
    }


    /*移除属性的方法*/
    $scope.removeFilterSearch = function (key){
        if ("brand"==key || key=='category' || key=="price"){
            $scope.searchMap[key]="";
        }else {
            delete $scope.searchMap.spec[key]
        }
        $scope.search();
    }
    /*根据情况判断显示的页数*/
    $scope.creatPage=function (){
        $scope.pageBox=[];
        var firstPage=1;
        var lastPagr=$scope.resultMap.totalPages;
        if ($scope.resultMap.totalPages <=5){
            /*如果总页数小于5，就不显示前后的点*/
            $scope.firstDot=false;
            $scope.lastDot=false;
        }
        if ($scope.resultMap.totalPages >5){
            if ($scope.searchMap.pageNo <=3){
                firstPage=1;
                lastPagr=5;
                $scope.firstDot=false;
                $scope.lastDot=true;
            }
            else if ($scope.searchMap.pageNo > $scope.resultMap.totalPages -2){
                firstPage = $scope.resultMap.totalPages -4;
                lastPagr = $scope.resultMap.totalPages;
                $scope.firstDot=true;
                $scope.lastDot=false;
            }
            else {
                firstPage=$scope.searchMap.pageNo-2;
                lastPagr=$scope.searchMap.pageNo+2;
                $scope.firstDot=true;
                $scope.lastDot=true;
            }
        }
        for (var i = firstPage; i <= lastPagr; i++) {
            $scope.pageBox.push(i);
        }
    }


    /*分页查询*/
    $scope.queryByPage=function (pageNo){
        pageNO=parseInt(pageNo);
        if (pageNo <1){
            pageNo =1;
        }
        if (pageNo > $scope.resultMap.totalPages){
            pageNo=$scope.resultMap.totalPages
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }


    /*排序*/
    $scope.addsort=function (sortField,sort){
        $scope.searchMap.sort=sort;

        $scope.searchMap.sortField=sortField;
        $scope.search();
    }


    $scope.search=function (){
        if ($scope.searchMap['keywords'] == ""){
            alert("请输入关键字")
            return ;
        }
        searchService.Itemsearch($scope.searchMap).success(
            function (response){
                $scope.resultMap=response;
                $scope.creatPage();
            }
        )
    }

    $scope.searchByKeyWords=function (){
        if ($scope.searchMap["keywords"] ==''){
            alert("请输入关键字")
            return ;
        }
        $scope.searchMap.pageNo=1;
        $scope.searchMap={'keywords':$scope.searchMap.keywords,"brand":'',"category":'',"spec":{},'price':"",'pageNo':1,'pageSize':10,'sortField':'','sort':''};
        $scope.search();
    }

    /*获取按键值*/
    $scope.getkeywords=function ($event){
        if ($event.keyCode==13){
            $scope.searchByKeyWords();
        }
    }

    /*判断搜索字段是否包含品牌*/
    $scope.keyWordsisBrand=function (){
        for (var i = 0; i < $scope.resultMap.brands.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brands[i]) >=0){
                return true;
            }
        }
        return  false;
    }
    /*接收页面数据*/
    $scope.searchKeywords=function (){
        $scope.searchMap.keywords=$location.search()["keywords"];
        $scope.search()
    }
})