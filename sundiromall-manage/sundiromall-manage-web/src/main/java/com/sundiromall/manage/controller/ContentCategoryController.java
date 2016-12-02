package com.sundiromall.manage.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sundiromall.manage.pojo.ContentCategory;
import com.sundiromall.manage.service.ContentCategoryService;

@Controller
@RequestMapping("content/category")
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;
    
    /**
     * 根据parentId查询内容列表
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ContentCategory>> queryContentCategoryList(
            @RequestParam(value = "id", defaultValue = "0") Long parentId) {
        try {
            ContentCategory category = new ContentCategory();
            category.setParentId(parentId);
            List<ContentCategory> contentCategories = contentCategoryService.queryByWhere(category);
            if (contentCategories == null || contentCategories.isEmpty()) {
                // 资源不存在,返回404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(contentCategories);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 服务器内部错误，返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 新增内容节点
     * @param contentCategory
     * @return
     */
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<ContentCategory> saveContentCategory(ContentCategory contentCategory) {
        try {
            //初始化未传入数据
            contentCategory.setStatus(1);
            contentCategory.setSortOrder(1);
            contentCategory.setIsParent(false);
            //新增节点
            this.contentCategoryService.save(contentCategory);
            //查询父子节点,并判断是不是父节点如果不是改为true
            ContentCategory parent = this.contentCategoryService.queryById(contentCategory.getParentId());
            if(!parent.getIsParent()){
                parent.setIsParent(true);
                this.contentCategoryService.updateSelective(parent);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(contentCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 重命名内容节点
     * @param contentCategory
     * @return
     */
    @RequestMapping(method=RequestMethod.PUT)
    public ResponseEntity<Void> updateContentCategory(ContentCategory contentCategory) {
        try {
            this.contentCategoryService.updateSelective(contentCategory);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 删除选中的内容节点及其子节点
     * @param contentCategory
     * @return
     */
    @RequestMapping(method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteContentCategorys(ContentCategory contentCategory) {
        try {
            //定义要被删除的节点的id的集合
            List<Object> ids = new ArrayList<>();
            ids.add(contentCategory.getId());
            //递归查询所有子点
            findAllSubNode(contentCategory.getId(), ids);
            this.contentCategoryService.deleteByIds(ids, "id", ContentCategory.class);
            
            // 查看当前节点的父节点是否还有其他子节点，如果没有，设置isParent为false
            ContentCategory param = new ContentCategory();
            param.setId(contentCategory.getParentId());
            List<ContentCategory> list = this.contentCategoryService.queryByWhere(param);
            if(list == null || list.isEmpty()) {
                //当前节点没e有子节点，将其的isParent的值设为false
                ContentCategory parent = new ContentCategory();
                parent.setId(contentCategory.getParentId());
                parent.setIsParent(false);
                this.contentCategoryService.updateSelective(parent);
            }
            
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 查询所有内容子节点
     * @param parentId
     * @param ids
     */
    private void findAllSubNode(Long parentId, List<Object> ids) {
        //查询当前节点
        ContentCategory params = new ContentCategory();
        params.setParentId(parentId);
        List<ContentCategory> list = this.contentCategoryService.queryByWhere(params);
        for (ContentCategory contentCategory : list) {
            ids.add(contentCategory.getId());
            if(contentCategory.getIsParent()) {
                //如果是父节点递归查询
                findAllSubNode(contentCategory.getId(), ids);
            }
        }
    }
}
