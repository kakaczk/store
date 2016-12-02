package com.sundiromall.search.mq.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundiromall.common.service.ApiService;
import com.sundiromall.search.bean.Item;

@Component
public class ItemMQHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemMQHandler.class);
    
    @Autowired
    private HttpSolrServer httpSolrServer;
    
    @Autowired
    private ApiService apiService;
    
    @Value("${SUNDIROMALL_MANAGE_URL}")
    private String SUNDIROMALL_MANAGE_URL;

    public void execute(String msg) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("接收到的消息，MSG={}", msg);
        }
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            Long itemId = jsonNode.get("itemId").asLong();
            String url = SUNDIROMALL_MANAGE_URL + "/rest/item/" + itemId;
            
            if(StringUtils.equals(jsonNode.get("type").asText(), "update") || StringUtils.equals(jsonNode.get("type").asText(), "insert")) {
                //往索引库中添加记录
                String jsonData = this.apiService.doGet(url);
                Item item = MAPPER.readValue(jsonData, Item.class);
                httpSolrServer.addBean(item);
            }else if(StringUtils.equals(jsonNode.get("type").asText(), "delete")) {
                //从索引库中删除记录
                httpSolrServer.deleteById(String.valueOf(itemId));
            }
            httpSolrServer.commit();
        } catch (Exception e) {
            LOGGER.error("处理消息出错！MSG=" + msg, e);
        }
    }
}
