package com.sundiromall.search.bean;

import java.util.List;

/**
 * 搜索结果pojo
 * 
 * @author kaka
 *
 * @param <T>
 */
public class PageInfo<T> {

    private Long total;
    
    private List<T> rows;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public PageInfo() {
    }
    
    public PageInfo(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }
    
}
