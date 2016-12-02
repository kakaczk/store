package com.sundiromall.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sundiromall.web.bean.Order;
import com.sundiromall.web.bean.User;
import com.sundiromall.web.handlerInterceptor.UserLoginHandlerInterceptor;
import com.sundiromall.web.service.ItemService;
import com.sundiromall.web.service.OrderService;
import com.sundiromall.web.threadLocal.UserThreadLocal;

@Controller
@RequestMapping("order")
public class OrderController {

    @Autowired
    private ItemService itemService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 去订单确认页，根据商品id查询商品信息
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ModelAndView toOrder(@PathVariable("itemId") Long itemId) {
        ModelAndView mv = new ModelAndView("order");
        mv.addObject("item", this.itemService.queryItemById(itemId));
        return mv;
    }
    
    /**
     * 提交订单
     * @param order
     * @param token
     * @return
     */
    @RequestMapping(value="submit", method=RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> submitOrder(Order order, 
            @CookieValue(UserLoginHandlerInterceptor.MALL_TOKEN) String token) {
        //从cookie中获得用户信息设置买家昵称
        User user = UserThreadLocal.get();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        
        //保存订单并返回订单号
        String orderId = this.orderService.submitOrder(order);
        if(null == orderId) {
            result.put("status", 400);
        } else {
            //订单创建成功
            result.put("status", 200);
            result.put("data", orderId);
        }
        return result;
    }
    
    /**
     * 显示订单成功页面（货到付款方式）
     * @param orderId
     * @return
     */
    @RequestMapping(value="success")
    public ModelAndView success(@RequestParam("id")String orderId) {
        ModelAndView mv = new ModelAndView("success");
        // 查询订单数据
        mv.addObject("order", this.orderService.queryOrderById(orderId));
        // 设置到货时间为当前时间往后推2天
        mv.addObject("date", new DateTime().plusDays(2).toString("MM月dd日"));
        return mv;
    }
}
