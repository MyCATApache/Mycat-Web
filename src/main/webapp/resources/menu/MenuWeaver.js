/**
 * j_obj is rainbow  json obj
 * 
 * j_obj = {rows:[{"id":"id","parentCode":"parentCode","text":"text","state":"folder/item","url":"url"}]};
 * 
 */
var MenuWeaver = function(areaId,j_obj){
	this.area = $("#"+areaId);
	this.menus = j_obj.rows;
};

/**
 * execute method
 */
MenuWeaver.prototype.weaver = function(){
	var root_menu_array = this._findDirectChildren(null);
	for(var i=0;i < root_menu_array.length; i++){
		this.area.append(this._createParent(root_menu_array[i],0));
	}
}


MenuWeaver.prototype._createLeaf = function(j_menu_leaf){
	var html = "<li><a href=\"javascript:loadContext('"+j_menu_leaf["url"]+"');\"><i class=\"fa fa-circle-o\"></i>"+j_menu_leaf["text"]+"</a></li>";
	return $(html);
}

MenuWeaver.prototype._createParent = function(j_menu_NoLeaf,level){
	var icon = "fa fa-dashboard";
	if(0==level){
		
	}else{
		icon = "fa fa-table";
	}
	var _html = '<li class="treeview"> <a href="#"><i class="'+icon+'"></i><span>'+j_menu_NoLeaf["text"]+'</span><i class="fa fa-angle-left pull-right"></i></a>';
	_html +='<ul class="treeview-menu"></ul></li>';
	//依据该parentCode获取其下一级别的对象
	var $parent = $(_html);
	var parent_sub = $parent.find(".treeview-menu");
	var subMenus = this._findDirectChildren(j_menu_NoLeaf);
	
	for(var i=0; i < subMenus.length; i++){
		if(subMenus[i]["state"] == 'folder'){
			parent_sub.append(this._createParent(subMenus[i],i+1));
		}else{
			parent_sub.append(this._createLeaf(subMenus[i]));
		}
	}
	return  $parent;
}

MenuWeaver.prototype._findDirectChildren = function(j_parent){
	var result_menus = [];
	$.each(this.menus,function(i,e){
		if(j_parent && e["parentCode"]){
			if(e["parentCode"] == j_parent["id"]){
				result_menus.push(e);
			}
		}else {
			if(!j_parent && !e["parentCode"])
				result_menus.push(e);
		}
	});
	return result_menus;
}
