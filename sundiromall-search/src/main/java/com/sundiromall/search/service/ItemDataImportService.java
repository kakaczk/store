package com.sundiromall.search.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sundiromall.common.bean.EasyUIResult;
import com.sundiromall.common.service.ApiService;
import com.sundiromall.search.bean.Item;

@Service
public class ItemDataImportService {

    @Autowired
    private ApiService apiService;
    
    @Autowired
    private HttpSolrServer HttpSolrServer;
    
    @Value("${SUNDIROMALL_MANAGE_URL}")
    private String SUNDIROMALL_MANAGE_URL;
    
    public String start() {
        // 1.从数据库中查询商品数据
        // 调用后台接口查询商品数据
        String url = SUNDIROMALL_MANAGE_URL + "/rest/item?page={page}&rows=100";
        // 分页获取数据
        Integer page = 1;
        Integer pageSize = 0;
        
        try {
            do {
                String jsonData = this.apiService.doGet(StringUtils.replace(url, "{page}", String.valueOf(page)));
                EasyUIResult easyUIResult = EasyUIResult.formatToList(jsonData, Item.class);
                @SuppressWarnings("unchecked")
                List<Item> rows = (List<Item>) easyUIResult.getRows();
                pageSize = rows.size();
                
                // 2.将商品数据写入solr库
                this.HttpSolrServer.addBeans(rows);
                this.HttpSolrServer.commit();
                
                page ++;
            } while (pageSize == 100);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}
