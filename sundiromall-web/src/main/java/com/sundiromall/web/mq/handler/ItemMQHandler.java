package com.sundiromall.web.mq.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundiromall.common.service.RedisService;
import com.sundiromall.web.service.ItemService;

@Component
public class ItemMQHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemMQHandler.class);

    @Autowired
    private RedisService redisService;

    public void execute(String msg) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("接收到的消息，MSG={}", msg);
        }
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            long itemId = jsonNode.get("itemId").asLong();
            redisService.del(ItemService.REDIS_ITEM_KEY + itemId);
        } catch (Exception e) {
            LOGGER.error("处理消息出错！MSG=" + msg, e);
        }
    }
}
