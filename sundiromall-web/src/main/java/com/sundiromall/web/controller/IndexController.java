package com.sundiromall.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sundiromall.web.service.IndexService;

@Controller
public class IndexController {
    
    @Autowired
    private IndexService indexService;

    @RequestMapping(value="index", method=RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("indexAd1", this.indexService.getIndexAd1());
        return mv;
    }
}
