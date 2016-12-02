package com.sundiromall.cart.handlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sundiromall.cart.pojo.User;
import com.sundiromall.cart.service.UserService;
import com.sundiromall.cart.threadLocal.UserThreadLocal;
import com.sundiromall.common.utils.CookieUtils;

public class UserHandlerInterceptor implements HandlerInterceptor {

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
            return true;
        }
        //通过sso接口查询用户信息
        User user = this.userService.queryUserByToken(token);
        if(null == user) {
            //用户登录信息已过期,跳转到登录页面重新登录
            response.sendRedirect(userService.SSO_MALL_URL + "/login.html");
            UserThreadLocal.set(null);
            return true;
        }
        //已登录放到本地线程中
        UserThreadLocal.set(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {

    }

}
