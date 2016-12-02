package com.sundiromall.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sundiromall.cart.pojo.User;
import com.sundiromall.cart.service.CartService;
import com.sundiromall.cart.threadLocal.UserThreadLocal;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;
    
    @RequestMapping(value="{itemId}", method=RequestMethod.GET)
    public String addItemToCart(@PathVariable("itemId") Long itemId) {
        //判断用户是否登录
        User user = UserThreadLocal.get();
        if(null == user) {
            //用户未登录
            
        }else {
            //用户已登录
            this.cartService.addItemToCart(itemId);
        }
        return null;
    }
}
