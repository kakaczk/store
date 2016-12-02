package com.sundiromall.manage.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundiromall.common.bean.ItemCatData;
import com.sundiromall.common.bean.ItemCatResult;
import com.sundiromall.common.service.RedisService;
import com.sundiromall.manage.pojo.ItemCat;

@Service
public class ItemCatService extends BaseService<ItemCat>{
    
/*    @Autowired
    private ItemCatMapper itemCatMapper;

    public List<ItemCat> queryItemCatList(long parentId) {
        // 根据父节点id查询商品类目
        ItemCat record = new ItemCat();
        record.setParentId(parentId);
        return itemCatMapper.select(record);
    }

    @Override
    public Mapper<ItemCat> getMapper() {
        return itemCatMapper;
    }
 */ 
    @Autowired
    private RedisService redisService;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    //添加缓存原则是不能影响正常业务逻辑,key命名规则：项目名_模块名_业务
    private static final String REDIS_KEY = "SUNDIROMALL_MANAGE_ITEMCAT_WEB_ALL";
    
    private static final Integer REDIS_TIME = 60*60*24*15;
    
    /**
     * 获取全部商品类目
     * @return
     */
    @SuppressWarnings("unchecked")
    public ItemCatResult queryAllTree() {
        try {
            //从缓存中命中
            String cacheData = redisService.get(REDIS_KEY);
            if(StringUtils.isNotEmpty(cacheData)) {
                return MAPPER.readValue(cacheData, ItemCatResult.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //递归方法查询商品分类列表
        @SuppressWarnings("rawtypes")
        List itemCatList = this.getItemCatList(0L);
        //返回结果
        ItemCatResult result = new ItemCatResult();
        result.setItemCats(itemCatList);
        
        //将查询到的数据放入到Redis缓存中,将数据序列化为json格式，设置缓存时间为15天
        try {
            redisService.set(REDIS_KEY, MAPPER.writeValueAsString(result), REDIS_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 递归获取商品类目
     * @param parentId
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getItemCatList(Long parentId) {
        ItemCat itemCat = new ItemCat();
        itemCat.setParentId(parentId);
        //根据id查询商品类目列表
        List<ItemCat> itemCats = this.queryByWhere(itemCat);
        List resultList = new ArrayList<>();
        for(ItemCat cat : itemCats) {
            //如果是父节点
            if(cat.getIsParent()) {
                ItemCatData catData = new ItemCatData();
                catData.setUrl("/products/"+cat.getId()+".html");
                //如果当前节点为第一级节点
                if(cat.getParentId() == 0) {
                    catData.setName("<a href='/products/"+cat.getId()+".html'>"+cat.getName()+"</a>");
                } else {
                    catData.setName(cat.getName());
                }
                //调用自身循环
                catData.setItems(getItemCatList(cat.getId()));
                //把ItemCatData对象添加到列表
                resultList.add(catData);
            } else {
                //如果是叶子节点
                String item = "/products/"+cat.getId()+".html|"+cat.getName();
                resultList.add(item);
            }
            //最多返回14个一级类目
            if (resultList.size() >= 14) {
                break;
            }

        }
        return resultList;
    }
}
