package com.sundiromall.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundiromall.common.bean.EasyUIResult;
import com.sundiromall.common.service.ApiService;
import com.sundiromall.manage.pojo.Content;

@Service
public class IndexService {

    @Value("${MANAGE_MALL_URL}")
    private String MANAGE_MALL_URL;
    
    @Value("${INDEX_AD1_URL}")
    private String INDEX_AD1_URL;
    
    @Autowired
    private ApiService apiService;
    
    public static final ObjectMapper MAPPER = new ObjectMapper();
    
/*    public Object getIndexAd1() {
        // 后台url
        String url = MANAGE_MALL_URL + INDEX_AD1_URL;
        try {
            // 通过调用后台系统获取数据
            String jsonData = this.apiService.doGet(url);
            // 解析json获取需要的数据，封装成前端需要的格式
            
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            ArrayNode arrayNode = (ArrayNode) jsonNode.get("rows");
            // 构造返回值List
            List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
            
            for (JsonNode node : arrayNode) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("srcB", node.get("pic").asText());
                map.put("height", 240);
                map.put("alt", node.get("title").asText());
                map.put("width", 670);
                map.put("src", map.get("srcB"));
                map.put("widthB", 550);
                map.put("href", node.get("url").asText());
                map.put("heightB", 240);
                
                result.add(map);
            }
            // 返回前端所需要的字符串（通过jackson工具包实现）
            return MAPPER.writeValueAsString(result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
*/
    
    @SuppressWarnings("unchecked")
    public Object getIndexAd1() {
        // 后台url
        String url = MANAGE_MALL_URL + INDEX_AD1_URL;
        try {
            // 通过调用后台系统获取数据
            String jsonData = this.apiService.doGet(url);
            // 解析json获取需要的数据，封装成前端需要的格式
            EasyUIResult easyUIResult = EasyUIResult.formatToList(jsonData, Content.class);
            List<Content> contents = (List<Content>) easyUIResult.getRows();
            // 构造返回值List
            List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
            
            for (Content content : contents) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("srcB", content.getPic());
                map.put("height", 240);
                map.put("alt", content.getTitle());
                map.put("width", 670);
                map.put("src", content.getPic());
                map.put("widthB", 550);
                map.put("href", content.getUrl());
                map.put("heightB", 240);
                
                result.add(map);
            }
            // 返回前端所需要的JSON字符串（通过jackson工具包实现）
            return MAPPER.writeValueAsString(result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    
}
