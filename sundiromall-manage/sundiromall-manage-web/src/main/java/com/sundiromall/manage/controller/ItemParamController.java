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

import com.github.pagehelper.PageInfo;
import com.sundiromall.common.bean.EasyUIResult;
import com.sundiromall.manage.pojo.ItemParam;
import com.sundiromall.manage.service.ItemParamService;

@RequestMapping("item/param")
@Controller
public class ItemParamController {

    @Autowired
    private ItemParamService itemParamService;
    
    /**
     * 根据类目id查询模板
     * @param itemCatId
     * @return
     */
    @RequestMapping(value="{itemCatId}", method=RequestMethod.GET)
    public ResponseEntity<ItemParam> queryItemParamByItemCatId(@PathVariable Long itemCatId) {
        try {
            ItemParam param = new ItemParam();
            param.setItemCatId(itemCatId);
            ItemParam itemParam = itemParamService.queryOne(param);
            if(null == itemParam) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(itemParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 新增规格参数模板
     * @param itemCatId
     * @param paramData
     * @return
     */
    @RequestMapping(value="{itemCatId}", method=RequestMethod.POST)
    public ResponseEntity<Void> saveItemParam(@PathVariable Long itemCatId,
            @RequestParam("paramData")String paramData) {
        try {
            ItemParam param = new ItemParam();
            param.setItemCatId(itemCatId);
            param.setParamData(paramData);
            itemParamService.save(param);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 查询规格参数列表
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public ResponseEntity<EasyUIResult> queryItemParamList(
            @RequestParam(value="page",defaultValue="1")Integer page,
            @RequestParam(value="rows", defaultValue="30")Integer rows){
        try {
            PageInfo<ItemParam> pageInfo = itemParamService.queryPageListByWhere(null, page, rows);
            return ResponseEntity.ok(new EasyUIResult(pageInfo.getTotal(), pageInfo.getList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 删除商品规格模板
     * @param ids
     * @return
     */
    @RequestMapping(method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteItemParamById(@RequestParam("ids")List<Object> ids) {
        try {
            this.itemParamService.deleteByIds(ids,"id",ItemParam.class);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 根据商品类目名称更新商品规格参数模板
     * @param cid
     * @param paramData
     * @return
     */
    @RequestMapping(method=RequestMethod.PUT)
    public ResponseEntity<Void> updateItemParamByCid(ItemParam itemParam) {
        try {
            this.itemParamService.updateSelective(itemParam);
            // 204，商品更新成功
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
     // 500，系统出现内部错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

