package com.sundiromall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sundiromall.search.service.ItemDataImportService;

/**
 * 搜索系统商品初始导入
 * @author kaka
 *
 */
@Controller
public class ItemDataImportController {
    
    @Autowired
    private ItemDataImportService itemDataImportService;

    @RequestMapping("item/importall")
    public String importAll() {
        String status = this.itemDataImportService.start();
        if(status == "1") {
            return "import_success";
        }else {
            return"import_error";
        }
    }
}
