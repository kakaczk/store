package com.sundiromall.web.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundiromall.common.service.ApiService;
import com.sundiromall.web.bean.User;

@Service
public class UserService {

    @Autowired
    private ApiService apiService;
    
    @Value("${SSO_MALL_URL}")
    public String SSO_MALL_URL;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    /**
     * 调用单点登录系统接口查询用户信息
     * @param token
     * @return
     */
    public User queryUserByToken(String token) {
        String url = SSO_MALL_URL + "/service/user/" + token;
        try {
            String jsonData = apiService.doGet(url);
            if(null == jsonData) {
                return null;
            }
            return MAPPER.readValue(jsonData, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
