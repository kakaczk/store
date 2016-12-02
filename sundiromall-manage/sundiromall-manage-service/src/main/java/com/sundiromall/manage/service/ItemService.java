package com.sundiromall.manage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageInfo;
import com.sundiromall.manage.pojo.Item;
import com.sundiromall.manage.pojo.ItemDesc;
import com.sundiromall.manage.pojo.ItemParamItem;

@Service
public class ItemService extends BaseService<Item> {

    @Autowired
    private ItemDescService itemDescService;
    
    @Autowired
    private ItemParamItemService itemParamItemService;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    //private ApiService apiService;
    
    @Value("${SUNDIROMALL_WEB_URL}")
    private String SUNDIROMALL_WEB_URL;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    /**
     * 添加商品
     * @param item
     * @param desc
     */
    public void save(Item item, String desc, String itemParams) {
        // 补全item信息
        item.setStatus(1);//设置状态
        item.setId(null); //出于系统安全考虑，强制id为null
        
        super.save(item);
        
        // 构建itemDesc
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemDesc(desc);
        itemDesc.setItemId(item.getId());
        itemDescService.save(itemDesc);
        
        //判断规格参数是否为空
        if(StringUtils.isNotEmpty(itemParams)) {
            ItemParamItem itemParamItem = new ItemParamItem();
            itemParamItem.setItemId(item.getId());
            itemParamItem.setParamData(itemParams);
            this.itemParamItemService.save(itemParamItem);
        }
        //发送商品的新增的消息到RabbitMQ
        this.sendMsg(item.getId(), "insert");
    }

    /**
     * 自定义条件分页查询商品信息
     * @param page
     * @param rows
     * @return
     */
    public PageInfo<Item> queryPageList(Integer page, Integer rows) {
        // 自定义条件
        Example example = new Example(Item.class);
        // 更新时间为倒序
        example.setOrderByClause("updated DESC");
        // 状态为没有被删除的status！=3
        example.createCriteria().andNotEqualTo("status", 3);
        return super.queryPageListByExample(example, page, rows);
    }

    /**
     * 根据条件删除商品集合
     * @param ids
     */
    public void deleteItemList(List<Object> ids) {
        Example example = new Example(Item.class);
        example.createCriteria().andIn("id", ids);
        Item item = new Item();
        item.setStatus(3);//将商品状态更改为3，做逻辑删除
        super.getMapper().updateByExampleSelective(item, example);
        
        //发送多条商品的删除的消息到RabbitMQ
        for (Object object : ids) {
            this.sendMsg(Long.valueOf(object.toString()), "delete");
        }
    }
    
    

    /**
     * 更新商品
     * @param item
     * @param desc
     */
    public void updateItem(Item item, String desc, ItemParamItem itemParamItem) {
        //强制设置不能被更新的的字段为null
        item.setStatus(null);
        item.setCreated(null);
        // 更新商品(字段为非空值)
        updateSelective(item);
        // 更新商品描述
        // 构建itemDesc
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemDesc(desc);
        itemDesc.setItemId(item.getId());
        itemDescService.updateSelective(itemDesc);
        // 判断规格参数是否为空
        if(null != itemParamItem) {
            //更新规格参数
            itemParamItemService.updateSelective(itemParamItem);
        }
        
        //发送商品的更新的消息到RabbitMQ
        this.sendMsg(item.getId(), "update");
        
        //通知其它系统商品已被更新，让缓存同步
/*        String url = SUNDIROMALL_WEB_URL + "/item/cache/" + item.getId() + ".html";
        try {
            this.apiService.doPost(url);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 根据条件下架商品集合
     * @param ids
     */
    public void instockItmeList(List<Object> ids) {
        Example example = new Example(Item.class);
        example.createCriteria().andIn("id", ids);
        Item item = new Item();
        item.setStatus(2);//将商品状态更改为2，做下架处理
        super.getMapper().updateByExampleSelective(item, example);
        
    }
    
    /**
     * 根据条件上架商品集合
     * @param ids
     */
    public void reshelfItmeList(List<Object> ids) {
        Example example = new Example(Item.class);
        example.createCriteria().andIn("id", ids);
        Item item = new Item();
        item.setStatus(1);//将商品状态更改为1，做上架处理
        super.getMapper().updateByExampleSelective(item, example);
        
    }
    
    /**
     * 发送消息队列方法
     * @param itemId
     * @param type
     */
    private void sendMsg(Long itemId, String type) {
        try {
            Map<String, Object> msg = new HashMap<>();
            msg.put("itemId", itemId);
            msg.put("type", type);
            msg.put("date", System.currentTimeMillis());            
            this.rabbitTemplate.convertAndSend("item." + type, MAPPER.writeValueAsString(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
