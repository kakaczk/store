package com.sundiromall.search.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sundiromall.search.bean.Item;
import com.sundiromall.search.bean.PageInfo;

@Service
public class ItemSearchService {
    
    @Autowired
    private HttpSolrServer httpSolrServer;
    
    public static final Integer ROWS = 24;

    public PageInfo<Item> search(String keyword, Integer page) throws SolrServerException {
        // 构造搜索对象和搜索关键字
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("title:" + keyword + " AND status:1");
        //设置分页
        solrQuery.setStart((Math.max(page, 1) - 1) * ROWS);
        solrQuery.setRows(ROWS);
        //是否需要高亮
        boolean isHighlighting = !StringUtils.equals("*", keyword) && StringUtils.isNotBlank(keyword);
        if(isHighlighting) {
            // 设置高亮
            solrQuery.setHighlight(true);  //开启高亮组件 
            solrQuery.addHighlightField("title");  //高亮字段
            solrQuery.setHighlightSimplePre("<em>");  //标记，高亮关键字前缀
            solrQuery.setHighlightSimplePost("</em>");  //后缀
        }
        // 执行查询
        QueryResponse queryResponse = this.httpSolrServer.query(solrQuery);
        List<Item> itemList = queryResponse.getBeans(Item.class);
        if(isHighlighting) {
            //将高亮的标题数据写回到数据对象中
            Map<String, Map<String, List<String>>> map = queryResponse.getHighlighting();
            for(Map.Entry<String, Map<String, List<String>>> highlighting : map.entrySet()) {
                for (Item item : itemList) {
                    if(!highlighting.getKey().equals(item.getId().toString())) {
                        continue;
                    }
                    item.setTitle(StringUtils.join(highlighting.getValue().get("title"), ""));
                    break;
                }
            }
        }
        return new PageInfo<Item>(queryResponse.getResults().getNumFound(),itemList);
    }

    
}
