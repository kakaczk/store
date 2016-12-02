package com.sundiromall.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.sundiromall.common.bean.EasyUIResult;
import com.sundiromall.manage.pojo.Content;
import com.sundiromall.manage.service.ContentService;

@Controller
@RequestMapping("content")
public class ContentController {

    @Autowired
    private ContentService contentService;
    
    /**
     * 新增内容
     * @return
     */
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Void> saveContent(Content content) {
        try {
            this.contentService.save(content);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 服务器内部错误，返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 查询内容列表，根据内容更新时间倒序显示 
     * @param categoryId
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public ResponseEntity<EasyUIResult> queryContentList(@RequestParam("categoryId")Long categoryId,
            @RequestParam(value="page",defaultValue="1")Integer page,
            @RequestParam(value="rows",defaultValue="10")Integer rows) {
        try {
            PageInfo<Content> pageInfo = this.contentService.queryContentListByUpdateDesc(categoryId, page, rows);
            return ResponseEntity.ok(new EasyUIResult(pageInfo.getTotal(), pageInfo.getList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 服务器内部错误，返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
