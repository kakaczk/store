package com.sundiromall.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sundiromall.manage.pojo.ItemParamItem;
import com.sundiromall.manage.service.ItemParamItemService;

@Controller
@RequestMapping("item/param/item")
public class ItemParamItemController {

    @Autowired
    private ItemParamItemService itemParamItemService;
    
    /**
     * 根据商品id查询商品规格
     * @param itemId
     * @return
     */
    @RequestMapping(value="{itemId}",method=RequestMethod.GET)
    public ResponseEntity<ItemParamItem> queryItemParamItemByItemId(@PathVariable Long itemId) {
        try {
            ItemParamItem paramItem = new ItemParamItem();
            paramItem.setItemId(itemId);
            ItemParamItem queryOne = this.itemParamItemService.queryOne(paramItem);
            if(null == queryOne) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(queryOne);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 500，系统出现内部错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
