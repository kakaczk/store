package com.sundiromall.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sundiromall.common.bean.ItemCatResult;
import com.sundiromall.manage.pojo.ItemCat;
import com.sundiromall.manage.service.ItemCatService;

@Controller
@RequestMapping("item/cat")
public class ItemCatController {

    @Autowired
    private ItemCatService itemCatService;

    /**
     * 查询商品类目树
     * 
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ItemCat>> queryItemCatList(
            @RequestParam(value = "id", defaultValue = "0") Long parentId) {
        try {
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);
            List<ItemCat> itemCats = itemCatService.queryByWhere(itemCat);
            if (itemCats == null || itemCats.isEmpty()) {
                // 资源不存在,返回404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(itemCats);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 服务器内部错误，返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 根据类目id查询类目信息
     * @param cid
     * @return
     */
    @RequestMapping(value="{cid}", method=RequestMethod.GET)
    public ResponseEntity<ItemCat> queryItemCatById(@PathVariable("cid") Long cid) {
        try {
            ItemCat itemCat = itemCatService.queryById(cid);
            if(null == itemCat) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(itemCat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 服务器内部错误，返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 查询属性叶子节点的itemCatList
     * @return
     */
    @RequestMapping(value="leaf",method=RequestMethod.GET)
    public ResponseEntity<List<ItemCat>> queryItemListNotParent() {
        try {
            ItemCat itemCat = new ItemCat();
            itemCat.setIsParent(false);
            List<ItemCat> itemCats = itemCatService.queryByWhere(itemCat);
            if (itemCats == null || itemCats.isEmpty()) {
                // 资源不存在,返回404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(itemCats);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 服务器内部错误，返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 按照前台要求返回所需的全部商品类目
     * @return
     */
    @RequestMapping(value="all",method=RequestMethod.GET)
    public ResponseEntity<ItemCatResult> queryItemCatResult() {
        return ResponseEntity.ok(this.itemCatService.queryAllTree());
    }
}
