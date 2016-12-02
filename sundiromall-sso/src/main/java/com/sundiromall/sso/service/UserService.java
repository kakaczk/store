package com.sundiromall.sso.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundiromall.common.service.RedisService;
import com.sundiromall.sso.mapper.UserMapper;
import com.sundiromall.sso.pojo.User;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RedisService redisService;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private static final Integer REDIS_TIME = 60 * 30;
    
    private static final Map<Integer, Boolean> TYPE = new HashMap<>();
    
    static {
        TYPE.put(1, true);
        TYPE.put(2, true);
        TYPE.put(3, true);
    }
    
    /**
     * 数据校验
     * @param param
     * @param type
     * @return
     */
    public Boolean check(String param, Integer type) {
        if(!TYPE.containsKey(type)) {
            return null;
        }
        User record = new User();
        switch (type) {
        case 1:
            record.setUsername(param);
            break;
        case 2:
            record.setPhone(param);
            break;
        case 3:
            record.setEmail(param);
            break;
        }
        User user = this.userMapper.selectOne(record);
        //返回 true:不可用    false:可用
        return user != null;
    }

    /**
     * 添加用户注册信息
     * @param user
     * @return
     */
    public Boolean saveUser(User user) {
        user.setId(null);
        user.setCreated(new Date());
        user.setUpdated(user.getCreated());
        //MD5加密
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        return this.userMapper.insert(user) == 1;
    }

    /**
     * 用户登录
     * @param user
     * @return
     * @throws Exception 
     */
    public String login(User param) throws Exception {
        User record = new User();
        record.setUsername(param.getUsername());
        User user = this.userMapper.selectOne(record);
        if(null == user){
            //用户不存在
            return null;
        }
        if(!user.getPassword().equals(DigestUtils.md5Hex(param.getPassword()))) {
            //密码不正确
            return null;
        }
        //以下为用户存在且密码正确
        String token = DigestUtils.md5Hex(System.currentTimeMillis() + user.getUsername());
        redisService.set("TOKEN_" + token, MAPPER.writeValueAsString(user), REDIS_TIME);
        return token;
    }

    /**
     * 根据token信息查询用户
     * @param token
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public User queryUserByToken(String token) throws Exception {
        String key = "TOKEN_" + token;
        String jsonData = this.redisService.get(key);
        if(null == jsonData) {
            //用户信息超时过期
            return null;
        }
        User user = MAPPER.readValue(jsonData, User.class);
        //重新设置用户生存时间
        this.redisService.expire(key, REDIS_TIME);
        return user;
    }

}
