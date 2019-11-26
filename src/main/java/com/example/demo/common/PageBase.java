package com.example.demo.common;

import java.util.Collections;

/**
 * 分页数据封装类
 */
public class PageBase {

    protected long total = 0; // 总页数
    protected int currentPage = 1; // 当前页数
    protected int pageSize = 10; // 每页显示多少条
    private Object vos;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Object getVos() {
        if (vos == null)
            return Collections.emptyList();
        return vos;
    }

    public void setVos(Object vos) {
        this.vos = vos;
    }
}
