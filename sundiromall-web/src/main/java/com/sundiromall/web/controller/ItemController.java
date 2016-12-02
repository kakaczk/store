package com.sundiromall.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sundiromall.manage.pojo.ItemDesc;
import com.sundiromall.web.bean.Item;
import com.sundiromall.web.service.ItemService;

@Controller
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;
    
    @RequestMapping(value="{itemId}", method=RequestMethod.GET)
    public ModelAndView showItemDetail(@PathVariable("itemId") Long itemId) {
        ModelAndView mv = new ModelAndView("item");
        //商品基本数据
        Item item = this.itemService.queryItemById(itemId);
        mv.addObject("item",item);
        //商品描述
        ItemDesc desc = this.itemService.queryItemDescByItemId(itemId);
        mv.addObject("itemDesc", desc);
        //商品规格参数
        String itemParamHTML = this.itemService.queryItemParamByItemId(itemId);
        mv.addObject("itemParam", itemParamHTML);
        
        return mv;
    }
}
