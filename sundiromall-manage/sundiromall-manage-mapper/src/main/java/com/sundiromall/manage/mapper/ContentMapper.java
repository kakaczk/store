package com.sundiromall.manage.mapper;

import java.util.List;

import com.github.abel533.mapper.Mapper;
import com.sundiromall.manage.pojo.Content;

public interface ContentMapper extends Mapper<Content> {

    public List<Content> queryContentListOrderByUpdateDesc(Long categoryId);

}
