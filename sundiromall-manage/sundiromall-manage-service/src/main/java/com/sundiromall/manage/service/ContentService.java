package com.sundiromall.manage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sundiromall.manage.mapper.ContentMapper;
import com.sundiromall.manage.pojo.Content;

@Service
public class ContentService extends BaseService<Content> {

    @Autowired
    private ContentMapper contentMapper;

    public PageInfo<Content> queryContentListByUpdateDesc(Long categoryId, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        return new PageInfo<Content>(this.contentMapper.queryContentListOrderByUpdateDesc(categoryId));
    }
    

}
