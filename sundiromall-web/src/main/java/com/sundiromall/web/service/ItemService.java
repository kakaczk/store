package com.sundiromall.web.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sundiromall.common.service.ApiService;
import com.sundiromall.common.service.RedisService;
import com.sundiromall.manage.pojo.ItemDesc;
import com.sundiromall.manage.pojo.ItemParamItem;
import com.sundiromall.web.bean.Item;

@Service
public class ItemService {

    @Autowired
    private ApiService apiService;
    
    @Autowired
    private RedisService redisService;

    @Value("${MANAGE_MALL_URL}")
    private String MANAGE_MALL_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private static final Integer REDIS_ITEM_TIME = 60*60*24;
    
    public static final String REDIS_ITEM_KEY = "SUNDIROMALL_WEB_ITEM_DETAIL";
        

    /**
     * 调用服务层方法查询商品详情
     * 
     * @param itemId
     * @return
     */
    public Item queryItemById(Long itemId) {
        //从缓存中命中
        String key = REDIS_ITEM_KEY + itemId;
        try {
            String json = this.redisService.get(key);
            if(StringUtils.isNotEmpty(json)) {
                return MAPPER.readValue(json, Item.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //正常查询逻辑
        String url = MANAGE_MALL_URL + "/rest/item/" + itemId;
        try {
            String jsonData = apiService.doGet(url);
            if (StringUtils.isEmpty(jsonData)) {
                return null;
            }
            
            //将查询到的数据缓存到redis
            try {
                this.redisService.set(key, jsonData, REDIS_ITEM_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return MAPPER.readValue(jsonData, Item.class);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据商品id查询商品描述
     * 
     * @param itemId
     * @return
     */
    public ItemDesc queryItemDescByItemId(Long itemId) {
        String url = MANAGE_MALL_URL + "/rest/item/desc/" + itemId;
        try {
            String jsonData = apiService.doGet(url);
            if (StringUtils.isEmpty(jsonData)) {
                return null;
            }
            return MAPPER.readValue(jsonData, ItemDesc.class);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据商品id查询商品规格参数
     * 
     * @param itemId
     * @return
     */
    public String queryItemParamByItemId(Long itemId) {
        String url = MANAGE_MALL_URL + "/rest/item/param/item/" + itemId;
        try {
            String jsonData = apiService.doGet(url);
            if (StringUtils.isNoneEmpty(jsonData)) {
                ItemParamItem itemParamItem = MAPPER.readValue(jsonData, ItemParamItem.class);
                String paramData = itemParamItem.getParamData();
                ArrayNode arrayNode = (ArrayNode) MAPPER.readTree(paramData);

                // 开始拼接html
                StringBuilder sb = new StringBuilder();
                sb.append(
                        "<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\"> <tbody>");
                for (JsonNode node : arrayNode) {
                    String group = node.get("group").asText();
                    sb.append("<tr><th class=\"tdTitle\" colspan=\"2\">" + group + "</th></tr>");
                    ArrayNode params = (ArrayNode) node.get("params");
                    for (JsonNode param : params) {
                        sb.append("<tr><td class=\"tdTitle\">" + param.get("k").asText() + "</td><td>"
                                + param.get("v").asText() + "</td></tr>");
                    }
                }
                sb.append("</tbody></table>");
                return sb.toString();

            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
