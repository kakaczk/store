package com.sundiromall.sso.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sundiromall.common.utils.CookieUtils;
import com.sundiromall.sso.pojo.User;
import com.sundiromall.sso.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    private static final String MALL_TOKEN = "M_TOKEN";
    
    //页面跳转到注册页面
    @RequestMapping(value="register", method=RequestMethod.GET)
    public String toRegister() {
        return "register";
    }
    
    //页面跳转到登录页面
    @RequestMapping(value="login", method=RequestMethod.GET)
    public String toLogin() {
        return "login";
    }
    
    /**
     * 用户注册校验
     * @param param
     * @param type
     * @return
     */
    @RequestMapping(value="user/check/{param}/{type}", method=RequestMethod.GET)
    public ResponseEntity<Boolean> check(@PathVariable("param")String param, @PathVariable("type")Integer type) {
        try {
            Boolean bool = this.userService.check(param,type);
            if(bool == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.ok(bool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 服务器内部错误，返回500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 注册用户
     * @param user
     * @param bindingResult
     * @return
     */
    @RequestMapping(value="user/doRegister", method=RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> register(@Valid User user, BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if(bindingResult.hasErrors()) {
            //输入的数据有误
            result.put("status", "400");
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            List<String> msg = new ArrayList<>();
            for (ObjectError objectError : allErrors) {
                msg.add(objectError.getDefaultMessage());
            }
            result.put("msg", StringUtils.join(msg, "|"));
            return result;
        }
        try {
            Boolean bool = this.userService.saveUser(user);
            if(bool) {
                result.put("status", "200");//此时的状态码200为业务状态码
            } else {
                result.put("status", "400");
            }
        } catch (Exception e) {
            result.put("status", "500");
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 用户登录
     * @param user
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="user/doLogin", method=RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> login(User user, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = this.userService.login(user);
            if(null == token) {
                result.put("status", 5);
            }else {
                result.put("status", 200);
                //如果成功找到将token信息写入到cookie中，级别为会话级别（浏览器关闭数据清除）
                CookieUtils.setCookie(request, response, MALL_TOKEN, token);
            }
        } catch (Exception e) {
            result.put("status", 4);
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 根据token查询用户信息
     * @param token
     * @return
     */
    @RequestMapping(value="user/{token}",method=RequestMethod.GET)
    public ResponseEntity<User> queryUserByToken(@PathVariable("token") String token) {
        try {
            User user = this.userService.queryUserByToken(token);
            if(null == user){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
}
