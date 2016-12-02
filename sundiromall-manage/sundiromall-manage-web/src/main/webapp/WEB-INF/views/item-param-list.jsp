<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="easyui-datagrid" id="itemParamList" title="商品规格模板列表" 
       data-options="singleSelect:false,collapsible:true,pagination:true,url:'/rest/item/param',method:'get',pageSize:30,toolbar:itemParamListToolbar">
    <thead>
        <tr>
        	<th data-options="field:'ck',checkbox:true"></th>
        	<th data-options="field:'id',width:60">ID</th>
        	<th data-options="field:'itemCatId',width:80">商品类目ID</th>
        	<th data-options="field:'itemCatName',width:100,formatter:formatItemCatName">商品类目</th>
            <th data-options="field:'paramData',width:300,formatter:formatItemParamData">规格(只显示分组名称)</th>
            <th data-options="field:'created',width:130,align:'center',formatter:TAOTAO.formatDateTime">创建日期</th>
            <th data-options="field:'updated',width:130,align:'center',formatter:TAOTAO.formatDateTime">更新日期</th>
        </tr>
    </thead>
</table>
<div id="itemParamEditWindow" class="easyui-window" title="编辑商品规格" data-options="modal:true,closed:true,iconCls:'icon-save',href:'/rest/page/item-param-edit'" style="width:80%;height:80%;padding:10px;">
</div>
<script>
	//定义全局变量存储商品类目名称
	var itemCatNames = [];
	//获取当前页所有规格参数模板并循环取出所有的商品类目名称，注意此处要把$.ajax函数中的异步请求关掉（默认开启）async:false
	$(function(){
		$.ajax({
			type:'get',
			url:'/rest/item/param',
			async:false,
			success:function(data){
				var _data = data.rows;
				for(var i in _data) {
					$.ajax({
						type:'get',
						url:'/rest/item/cat/'+_data[i].itemCatId,
						async:false,
						success:function(xdata){
							itemCatNames.push(xdata.name);
						}
					})
				}
			}
		})
	});
	
	//添加分组(group)方法
	function fun1(obj){
		var temple = $(".itemParamEditTemplate li").eq(0).clone();
		//$(obj).parent().append(temple);
		//将输入框追加到现有的li后面
		$(".editGroupTr li[class=param]:last").append(temple);
		temple.find(".addParam").click(function() {
			var li = $(".itemParamEditTemplate li").eq(2).clone();
			li.find(".delParam").click(function() {
				$(this).parent().remove();
			});
			li.appendTo($(this).parentsUntil("ul").parent());
		});
		temple.find(".delParam").click(function() {
			$(this).parent().remove();
		});
	}
	//添加分组子项（param）方法
	function fun2(obj) {
		var li = $(".itemParamEditTemplate li").eq(2).clone();
		li.find(".delParam").click(function() {
			$(this).parent().remove();
		});
		//追加子项到现有子项后面
		li.appendTo($(obj).parentsUntil("ul").parent());
	}
	//删除分组及子项方法
	function fun3(obj) {
		$(obj).parent().remove();
	}
	
	function formatItemCatName(value,data, index) {
		return itemCatNames[index];
	}
	
	function formatItemParamData(value , index){
		var json = JSON.parse(value);
		var array = [];
		$.each(json,function(i,e){
			array.push(e.group);
		});
		return array.join(",");
	}

    function getSelectionsIds(){
    	var itemList = $("#itemParamList");
    	var sels = itemList.datagrid("getSelections");
    	var ids = [];
    	for(var i in sels){
    		ids.push(sels[i].id);
    	}
    	ids = ids.join(",");
    	return ids;
    }
    
    var itemParamListToolbar = [{
        text:'新增',
        iconCls:'icon-add',
        handler:function(){
        	TAOTAO.createWindow({
        		url : "/rest/page/item-param-add",
        	});
        }
    },{
        text:'编辑',
        iconCls:'icon-edit',
        handler:function(){
        	var ids = getSelectionsIds();
        	if(ids.length == 0){
        		$.messager.alert('提示','必须选择一个商品规格模板才能编辑!');
        		return ;
        	}
        	if(ids.indexOf(',') > 0){
        		$.messager.alert('提示','只能选择一个模板!');
        		return ;
        	}
        	$("#itemParamEditWindow").window({
        		onLoad :function(){
        			$(".editGroupTr").show();
        			var data = $("#itemParamList").datagrid("getSelections")[0];
        			//将商品规格模板id传递给修改页面
        			$("#itemParamEditTable [name='paramid']").val(ids[0]);
        			
        			//将商品类目id传递给修改页面
        			$("#itemParamEditTable [name='cid']").val(data.itemCatId);
        			// 加载商品类目名称,替换common.js中的类目名称,回显
        			$.getJSON('/rest/item/cat/'+data.itemCatId,function(_data){
        				$("#categoryName").html(_data.name);
        			});
        			
        		    //加载商品规格
        			$.getJSON('/rest/item/param/'+data.itemCatId,function(_data){
        				//转换为json格式
						var paramData = JSON.parse(_data.paramData);
						var html = "<ul>";
						html += '<li><a href="javascript:void(0)" class="easyui-linkbutton editGroup l-btn-small"><span class="l-btn-left" onclick="fun1(this)">添加分组</span></a></li>';
						for(var i=0; i<paramData.length; i++){
							var group = paramData[i]['group'];
							html+="<li class=\"param\"><ul><li>";
							html+="<input class=\"easyui-textbox\" style=\"width: 150px;\" name=\"group\" value="+group+" />&nbsp;<a href=\"javascript:void(0)\" class=\"easyui-linkbutton addParam\" title=\"添加参数\" data-options=\"plain:true,iconCls:\'icon-add\'\" onclick=\"fun2(this)\"></a>";
							html+="</li>";
							var params = paramData[i]['params'];
							for(var j=0; j<params.length; j++) {
								var param = params[j];
								html+="<li><span>|-------</span><input style=\"width: 150px;\" class=\"easyui-textbox\" name=\"param\" value="+param+" />&nbsp;";
								html+="<a href=\"javascript:void(0)\" class=\"easyui-linkbutton delParam\" title=\"删除\" data-options=\"plain:true,iconCls:'icon-cancel'\" onclick=\"fun3(this)\"></a>";
								html+="</li>";
							}
							html+="</ul></li>";
						}
						html+= "</li></ul>";
   					 	//在当前网页加入动态html片断，刷新当前网页
   					 	$.parser.parse($(".editGroupTr td:eq(1)").html(html));
        			});
        			TAOTAO.init({
        				"cid" : data.itemCatId,
        				fun:function(node){
        					TAOTAO.changeItemParam(node, "itemParamEditTable");
        				}
        			}); 
        		}
        	}).window("open");
        }
    },{
        text:'删除',
        iconCls:'icon-cancel',
        handler:function(){
        	var ids = getSelectionsIds();
        	if(ids.length == 0){
        		$.messager.alert('提示','未选中商品规格!');
        		return ;
        	}
        	$.messager.confirm('确认','确定删除ID为 '+ids+' 的商品规格吗？',function(r){
        	    if (r){
        	    	var params = {"ids":ids,"_method":"DELETE"};
        	    	//提交到后台的RESTful
        			$.ajax({
        			   type: "POST",
        			   url: "/rest/item/param",
        			   data: params,
        			   statusCode:{
        				   204:function(){
        					   $.messager.alert('提示','删除商品规格成功!',undefined,function(){
               						$("#itemParamList").datagrid("reload");
               				   });
        				   },
        				   500:function(){
        					   $.messager.alert('提示','删除商品规格失败!');
        				   }
        			   }
        			});
        	    }
        	});
        }
    }];
    
 
    
</script>