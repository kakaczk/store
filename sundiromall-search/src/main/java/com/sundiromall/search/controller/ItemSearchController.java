package com.sundiromall.search.controller;

import java.io.UnsupportedEncodingException;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sundiromall.search.bean.Item;
import com.sundiromall.search.bean.PageInfo;
import com.sundiromall.search.service.ItemSearchService;

/**
 * 商品搜索
 * @author kaka
 *
 */
@Controller
public class ItemSearchController {

    @Autowired
    private ItemSearchService itemSearchService;
    
    @RequestMapping(value="search", method=RequestMethod.GET)
    public ModelAndView search(@RequestParam("keyword")String keyword,
            @RequestParam(value="page",defaultValue="1")Integer page) {
        try {
            keyword = new String(keyword.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        ModelAndView mView = new ModelAndView();
        mView.addObject("query", keyword);
        mView.addObject("page", page);
        try {
            PageInfo<Item> pageInfo = this.itemSearchService.search(keyword, page);
            mView.addObject("itemList", pageInfo.getRows());
            
            mView.addObject("pages", (pageInfo.getTotal() + ItemSearchService.ROWS - 1)/ ItemSearchService.ROWS);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return mView;
    }
}
