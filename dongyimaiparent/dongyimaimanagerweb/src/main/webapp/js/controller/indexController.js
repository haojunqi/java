app.controller('indexController',function ($scope,loginService){
    $scope.getName=function (){
        loginService.getName().success(
            function (resopnse){
                $scope.username=resopnse.loginName
            }
        )
    }
})