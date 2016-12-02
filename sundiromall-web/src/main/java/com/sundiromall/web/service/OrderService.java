package com.sundiromall.web.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundiromall.common.bean.HttpResult;
import com.sundiromall.common.service.ApiService;
import com.sundiromall.web.bean.Order;

@Service
public class OrderService {

    @Value("${ORDER_MALL_URL}")
    private String ORDER_MALL_URL;
    
    @Autowired
    private ApiService apiService;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 调用订单系统的创建订单接口进行提交订单
     * @param order
     * @return 如果成功返回订单号，如果失败返回null
     */
    public String submitOrder(Order order) {
        String url = ORDER_MALL_URL + "/order/create";
        try {
            String jsonData = MAPPER.writeValueAsString(order);
            HttpResult httpResult = this.apiService.doPostJson(url, jsonData);
            //httpResult状态码
            if(httpResult.getCode() == 200) {
                //取得httpResult数据(SundiroResult)
                JsonNode jsonNode = MAPPER.readTree(httpResult.getData());
                if(jsonNode.get("status").asInt() == 200) {
                    //订单创建成功,返回订单号
                    return jsonNode.get("data").asText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据订单id调用订单接口查询订单数据
     * @param orderId
     * @return
     */
    public Order queryOrderById(String orderId) {
        String url = ORDER_MALL_URL + "/order/query/" + orderId;
        try {
            String jsonData = this.apiService.doGet(url);
            if(jsonData != null) {
                return MAPPER.readValue(jsonData, Order.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }
    
    
}
