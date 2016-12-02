package com.sundiromall.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sundiromall.common.service.RedisService;
import com.sundiromall.web.service.ItemService;

@Controller
@RequestMapping("item/cache")
public class ItemCacheController {

    @Autowired
    private RedisService redisService;
    
    /**
     * 根据商品id删除缓存数据
     * @param itemId
     * @return
     */
    @RequestMapping(value="{itemId}", method=RequestMethod.POST)
    public ResponseEntity<Void> deleteCache(@PathVariable("itemId") Long itemId) {
        try {
            this.redisService.del(ItemService.REDIS_ITEM_KEY + itemId);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 服务器内部错误，返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
