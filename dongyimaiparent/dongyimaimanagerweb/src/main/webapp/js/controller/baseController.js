app.controller('baseController',function ($scope) {
    $scope.selectIds=[];
    $scope.reloadList=function (){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }
    /*添加分页配置控件*/
    $scope.paginationConf={
        currentPage:1,  /*当前页码*/
        totalItems:10,  /*数据总条数*/
        itemsPerPage:10, /**/
        perPageOptions:[10,20,30,40,50],
        onChange:function (){
            $scope.reloadList();
            $scope.checkbox=false
            $("#selall").prop("checked",false);
        }
    }

    $scope.updateSelectIds=function ($event,id){
        if($event.target.checked){
            $scope.selectIds.push(id)
        }else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);
            $("#selall").prop("checked",false);

        }
    }
})
