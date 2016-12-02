package com.sundiromall.manage.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.sundiromall.common.bean.EasyUIResult;
import com.sundiromall.manage.pojo.Item;
import com.sundiromall.manage.pojo.ItemParamItem;
import com.sundiromall.manage.service.ItemService;

@Controller
@RequestMapping("item")
public class ItemController {

    public static final Logger LOGGER = LoggerFactory.getLogger(Logger.class);
    
    @Autowired
    private ItemService itemService;
    
    /**
     * 添加商品方法
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Void> save(Item item, @RequestParam("desc")String desc, @RequestParam("itemParams") String itemParams) {
        try {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("添加商品，item={},desc={}", item, desc);
            }
            
            itemService.save(item, desc, itemParams);
            
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("添加商品成功，itemId={}", item.getId());
            }
            // 201，商品添加成功
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("添加商品失败！item="+item, e);
        }
        // 500，系统出现内部错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    /**
     * 分页查询商品信息
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public ResponseEntity<EasyUIResult> queryItemList(
            @RequestParam(value="page",defaultValue="1")Integer page,
            @RequestParam(value="rows",defaultValue="30")Integer rows) {
        try {
            PageInfo<Item> pageInfo = itemService.queryPageList(page, rows);
            return ResponseEntity.ok(new EasyUIResult(pageInfo.getTotal(), pageInfo.getList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 根据商品id查询商品详情
     * @param itemId
     * @return
     */
    @RequestMapping(value="{itemId}", method=RequestMethod.GET)
    public ResponseEntity<Item> queryItemById(@PathVariable("itemId") Long itemId) {
        try {
            Item item = itemService.queryById(itemId);
            if(null == item) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 删除商品方法
     * @param ids
     * @return
     */
    @RequestMapping(method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteItemList(@RequestParam("ids")List<Object> ids) {
        try {
            itemService.deleteItemList(ids);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 商品更新
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping(method=RequestMethod.PUT)
    public ResponseEntity<Void> update(Item item, @RequestParam("desc")String desc
            , @RequestParam("itemParams") String itemParams, @RequestParam("itemParamId") Long itemParamId) {
        try {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("更新商品，item={},desc={}", item, desc);
            }
            //判断规格参数id是否为空
            ItemParamItem itemParamItem = null;
            if(null != itemParamId) {
                itemParamItem = new ItemParamItem();
                itemParamItem.setId(itemParamId);
                itemParamItem.setParamData(itemParams);
            }
            itemService.updateItem(item, desc, itemParamItem);
            
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("更新商品成功，itemId={}", item.getId());
            }
            // 204，商品更新成功
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("更新商品失败！item="+item, e);
        }
        // 500，系统出现内部错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    /**
     * 商品下架
     * @param ids
     * @return
     */
    @RequestMapping(value="instock", method=RequestMethod.POST)
    public ResponseEntity<Void> instock(@RequestParam("ids")List<Object> ids) {
        try {
            itemService.instockItmeList(ids);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    /**
     * 商品上架
     * @param ids
     * @return
     */
    @RequestMapping(value="reshelf", method=RequestMethod.POST)
    public ResponseEntity<Void> reshelf(@RequestParam("ids")List<Object> ids) {
        try {
            itemService.reshelfItmeList(ids);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
