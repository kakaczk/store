package com.sundiromall.web.handlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sundiromall.common.utils.CookieUtils;
import com.sundiromall.web.bean.User;
import com.sundiromall.web.service.UserService;
import com.sundiromall.web.threadLocal.UserThreadLocal;

public class UserLoginHandlerInterceptor implements HandlerInterceptor {

    public static final String MALL_TOKEN = "M_TOKEN";
    
    @Autowired
    private UserService userService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 从cookie中获取token的值
        String token = CookieUtils.getCookieValue(request, MALL_TOKEN);
        if(null == token) {
            //没有用户登录信息,跳转到登录页面
            response.sendRedirect(userService.SSO_MALL_URL + "/login.html");
            UserThreadLocal.set(null);
            return false;
        }
        //通过sso接口查询用户信息
        User user = this.userService.queryUserByToken(token);
        if(null == user) {
            //用户登录信息已过期,跳转到登录页面重新登录
            response.sendRedirect(userService.SSO_MALL_URL + "/login.html");
            UserThreadLocal.set(null);
            return false;
        }
        //已登录放到本地线程中
        UserThreadLocal.set(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        // TODO Auto-generated method stub

    }

}
