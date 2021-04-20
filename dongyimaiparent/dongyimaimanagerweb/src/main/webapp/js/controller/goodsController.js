 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location  ,typeTemplateService,itemCatService,brandService,uService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.brandList={data:[]}
	$scope.image_entity={};
	$scope.entity={tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]},tbItemList:[]};
	$scope.ItemCat1List={}
	$scope.ItemCatList=[];
	$scope.statusList=["未审核","审核通过","审核未通过"]
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}

	$scope.findItemCatList=function (){
		itemCatService.findAll().success(
			function (response){
				for (var i = 0; i < response.length; i++) {
					$scope.ItemCatList[response[i].id]=response[i].name
				}
			}
		)
	}

	/*修改审核状态码*/
	$scope.updateStatus=function (status){
		goodsService.updateStatus($scope.selectIds,status).success(
			function (response){
				if (response.success){
				alert("审核提交完成");
				$scope.selectIds=[];
				console.info("执行"+$scope.selectIds)
				$scope.reloadList();
				}else {
					alert("提交失败")
				}

			}
		)
	}




	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id = $location.search()['id']
		if(id ==null){
			return ;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity=response
				editor.html($scope.entity.tbGoodsDesc.introduction);
				$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
				$scope.entity.tbGoodsDesc.specificationItems=JSON.parse($scope.entity.tbGoodsDesc.specificationItems)
				for (var i = 0; i < $scope.entity.tbItemList.length; i++) {
					$scope.entity.tbItemList[i].spec=JSON.parse($scope.entity.tbItemList[i].spec)
				}
			$scope.entity.tbGoodsDesc.itemImages=JSON.parse($scope.entity.tbGoodsDesc.itemImages);
				console.info(response)
			}
		);				
	}

	/*规格选项是否被勾选*/

	$scope.add=function (){
		var serviceObject;
		$scope.entity.tbGoodsDesc.introduction=editor.html()
		if ($scope.entity.tbGoods.id != null){
			serviceObject=goodsService.update($scope.entity);
		}else {
			serviceObject=goodsService.add($scope.entity);
		}
		serviceObject.success(
		function(response){
			if(response.success){
				alert("添加成功")
				$scope.entity = {tbGoods:{},tbGoodsDesc:{},tbItemList:[]};
				editor.html('');
			}else{
				alert(response.message);
			}
		}
	);


}



	/*获取登录用户的用户名*/
	$scope.loginn=function (){
		goodsService.login().success(
			function (resopnse){
				console.info(resopnse);
				$scope.userName = resopnse.userName
			}
		)
	}
	//保存 
/*	$scope.save=function(){
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}*/
	
	 
	//批量删除
	/*删除商品*/
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.select2BrandList=function (){
		brandService.select2BrandList().success(
			function (response){
				$scope.brandList={data: response}
			}
		)
	}
	$scope.loadFile=function (){
		uService.uploadFile().success(
			function (response){
				$scope.image_entity.url=response.message;
			}
		)
	}
	$scope.addImages = function(){
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
		console.log($scope.entity.tbGoodsDesc.itemImages.length + "-------");
	}

	/*查询一级下拉框的数据*/
		$scope.selectItemCat1List=function (){
			itemCatService.selectItemCat1(0).success(
				function (response){
					$scope.ItemCat1List=response;
				}
			)
		}
		/*查询二级下拉框的数据*/
		$scope.$watch('entity.tbGoods.category1Id',function (newValue,oldVale){
			if(newValue){
				itemCatService.selectItemCat1(newValue).success(
					function (response){
						$scope.ItemCat2List=response;
						$scope.ItemCat3List=[]
						$scope.entity.tbGoods.typeTemplateId="";
					}
				)
			}
		})

	/*查询三级下拉框的数据*/
	$scope.$watch('entity.tbGoods.category2Id',function (newValue,oldVale){
		if(newValue){
			itemCatService.selectItemCat1(newValue).success(
				function (response){
					$scope.ItemCat3List=response;
					$scope.entity.tbGoods.typeTemplateId="";
				}
			)
		}
	})

	/*监听三级列表的变化获取模板的id*/
	$scope.$watch('entity.tbGoods.category3Id',function (newValue,oldValue){
		if(newValue){
			itemCatService.findOne(newValue).success(
				function (response){
					$scope.entity.tbGoods.typeTemplateId=response.typeId
				}
			)
		}
	})

	/*监听模板id的事件，获取品牌的数据*/
	$scope.$watch('entity.tbGoods.typeTemplateId',function (newValue,oldVlaue){
		if (newValue != "" && newValue != null){
			typeTemplateService.findOne(newValue).success(
				function (response){
					$scope.typeTemplate=response
					//json格式数据转换
					$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds)
					if ($location.search()['id'] ==null){

					$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
					}
				}
			)

			/*根据*/
			typeTemplateService.findSpecList(newValue).success(
				function (response){
					$scope.specList=response;
				}
			)
		}
	})

/*	$scope.createItemList=function (){
		$scope.entity.itemList=[{spec:{},price:0,num:10000,status:'0',isDefault:'0'}]
		var items = $scope.entity.tbGoodsDesc.specificationItems;
		for (var i =0;i<items.length;i++){
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
		}
	}

	addColumn=function (list,columnName,columnValue){
		alert("执行克隆")
		var newList=[];
		for (var i = 0;i<list.length;i++){
			var row=list[i];
			for (var j=0;j<columnValue.length;j++){
				var newRow=JSON.parse(JSON.stringify(row))
				newRow.spec[columnName] = columnValue[j];
				newList.push(newRow)
			}
		}
		return newList;
	}


	$scope.searchObjecBytKey=function (list,key,value){
		for (var i =0;i<list.length;i++){
			if(list[i][key] == value){
				return list[i]
			}
		}
		return  null;
	}

	$scope.selectSpecAttribute=function ($event,name,value){
		var obj=$scope.searchObjecBytKey($scope.entity.tbGoodsDesc.specificationItems,"attributesName",name)
		if(obj ==null){
			$scope.entity.tbGoodsDesc.specificationItems.push({"attributeValue":[value]})
		}else {
			if($event.target.checked){
				obj.attributeValue.push(value);
			}else {
				obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
				if(obj.attributeValue.length ==0){
					$scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(obj),1)
				}
			}
		}
		$scope.createItemList();
	}*/
	$scope.createItemList = function(){
		$scope.entity.tbItemList = [{spec:{},price:0,num:10000,status:'0',isDefault:'0'}];
		var items = $scope.entity.tbGoodsDesc.specificationItems;
		for(var i = 0; i <items.length; i++){
			$scope.entity.tbItemList = addColumn($scope.entity.tbItemList,items[i].attributeName,items[i].attributeValue);
		}



	}

	// 添加要显示的字段
	addColumn = function(list,columnName,columnValue){
		var newlist = [];
		for(var i = 0; i < list.length; i++){
			// 获取每一个元素
			var row = list[i];
			for(var j = 0; j < columnValue.length; j++){
				var newRow = JSON.parse(JSON.stringify(row));
				newRow.spec[columnName] = columnValue[j];
				newlist.push(newRow);
			}
		}
		return newlist;
	}


	//确认集合中是否存储了对应的键值对
	// 存储有，则返回对应的存储对象，如果没有，返回null
	$scope.searchObjectByKey = function(list,key,value){
		for(var i = 0; i < list.length; i++){
			if(list[i][key] == value){
				return list[i];
			}
		}
		return null;
	}

	$scope.checkAttribute=function (specName,optionName){
		var item = $scope.entity.tbGoodsDesc.specificationItems;
		var Object=$scope.searchObjectByKey(item,'attributeName',specName)
		if (Object ==null){
			return false
		}else {
			if (Object.attributeValue.indexOf(optionName)>=0){
				return true
			}else {
				return false
			}
		}
	}

	// 选项的点击事件(被选中和取消)
	// name 表示规格名  value表示规格项
	$scope.selectSpecAttribute = function($event,name,value){
		// 规格项 在 specificationItems 是否存在
		var obj = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,"attributeName",name);
		// 第一次出现该规格
		if(obj == null){
			$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}else{ // 之前存储过对应的规格
			// 选中状态
			if($event.target.checked){
				obj.attributeValue.push(value);
			}else{ // 取消的状态
				obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
				if(obj.attributeValue.length == 0){
					$scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(obj),1);
				}
			}


		}

		$scope.createItemList();

	}

    
});	