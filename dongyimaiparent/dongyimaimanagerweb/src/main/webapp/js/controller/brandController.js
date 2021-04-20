app.controller('brandcontroller',function ($scope,$controller,brandService){
    $controller('baseController',{$scope:$scope});//继承
    $scope.searchEntity={};
    $scope.selectAll=function ($event){
        $scope.checkbox=$event.target.checked;
        if($event.target.checked){
            angular.forEach($scope.list,function (entity){
                $scope.selectIds.push(entity.id)
                console.info($scope.selectIds)
            })
        }else {
            $scope.selectIds=[];
        }
    }
    $scope.findAll = function (){
        brandService.findAll().success(
            function (response){
                $scope.list=response
            })
    }
    $scope.findPage=function (page,rows){
        brandService.findPage().success(
            function (response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }
    /*添加新品牌的函数*/
    $scope.save=function (){
        var methodName ='add';
        if($scope.entity.id != null){
            methodName = 'update';
        }
        brandService.save(methodName,$scope.entity).success(
            function (response){
                if (response.success){
                    $scope.reloadList()
                }else{
                    alert(response.message)
                }
            }

        )
    }
    $scope.findone=function (id){
        brandService.findone(id).success(
            function (resopnse){
                $scope.entity=resopnse;
            }
        )
    }
    /*删除*/
    $scope.dele = function (){
        brandService.dele($scope.selectIds).success(
            function (response){
                if(response.success){
                    $scope.checkbox=false;
                    $scope.reloadList();
                    $scope.selectIds=[];
                }
            }
        )
    }
    $scope.search=function (page,rows){
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response){
                $scope.paginationConf.totalItems=response.total
                $scope.list = response.rows
            }
        )
    }
})