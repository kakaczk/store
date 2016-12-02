package com.sundiromall.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 页面管理
 * @author kaka
 *
 */
@Controller
@RequestMapping("page")
public class PageController {

    /**
     * 通用页面跳转方法
     * @param pagename
     * @return
     */
    @RequestMapping(value="{pagename}", method=RequestMethod.GET)
    public String page(@PathVariable String pagename) {
        return pagename;
    }
}
