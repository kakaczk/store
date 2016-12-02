<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<table cellpadding="5" style="margin-left: 30px" id="itemParamEditTable"
	class="itemParam">
	<tr>
		<td>商品类目:</td>
		<td><a href="javascript:void(0)"
			class="easyui-linkbutton selectItemCat">选择类目</a> 
			<input type="hidden" name="cid" style="width: 280px;"></input>
			<input type="hidden" name="paramid"></input>
		</td>
	</tr>
	<tr class="hide editGroupTr">
		<td>规格参数:</td>
		<td>
			<ul>
				<li><a href="javascript:void(0)"
					class="easyui-linkbutton editGroup">添加分组</a></li>
			</ul>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><a href="javascript:void(0)" class="easyui-linkbutton submit">提交</a>
			<a href="javascript:void(0)" class="easyui-linkbutton close">关闭</a></td>
	</tr>
</table>
<div class="itemParamEditTemplate" style="display: none;">
	<li class="param">
		<ul>
			<li><input class="easyui-textbox" style="width: 150px;"
				name="group" />&nbsp;<a href="javascript:void(0)"
				class="easyui-linkbutton addParam" title="添加参数"
				data-options="plain:true,iconCls:'icon-add'"></a></li>
			<li><span>|-------</span><input style="width: 150px;"
				class="easyui-textbox" name="param" />&nbsp;<a
				href="javascript:void(0)" class="easyui-linkbutton delParam"
				title="删除" data-options="plain:true,iconCls:'icon-cancel'"></a></li>
		</ul>
	</li>
</div>
<script>
	$(function() {

		$(".editGroup").click(function() {
			var temple = $(".itemParamEditTemplate li").eq(0).clone();
			$(this).parent().parent().append(temple);
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
		});

		$("#itemParamEditTable .close").click(function() {
			$(".panel-tool-close").click();
		});

		$("#itemParamEditTable .submit").click(
				function() {
					var params = [];
					var groups = $("#itemParamEditTable [name=group]");
					groups.each(function(i, e) {
						var p = $(e).parentsUntil("ul").parent().find(
								"[name=param]");
						var _ps = [];
						p.each(function(_i, _e) {
							var _val = $(_e).siblings("input").val();
							if ($.trim(_val).length > 0) {
								_ps.push(_val);
							}
						});
						var _val = $(e).siblings("input").val();
						if ($.trim(_val).length > 0 && _ps.length > 0) {
							params.push({
								"group" : _val,
								"params" : _ps
							});
						}
					});
					//组装java bean
					var itemParam = {};
					//设置各参数
					itemParam.id = $("input[name='paramid']").val();
					itemParam.itemCatId = $("input[name='cid']").val();
					itemParam.paramData = JSON.stringify(params);
					//提交到后台的RESTful
					$.ajax({
					   type: "PUT",
					   url: "/rest/item/param/",
					   data: itemParam,
					   dataType: "json",
					   success: function(data) {
							$.messager.alert('提示', '修改商品规格成功!', undefined,
									function() {
										$(".panel-tool-close").click();
										$("#itemParamList").datagrid("reload");
									});
					   },
					   error: function(){//500
						   $.messager.alert('提示','修改商品规格失败!');
					   }
					});
				});
		});
</script>