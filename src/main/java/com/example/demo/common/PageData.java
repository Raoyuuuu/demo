package com.example.demo.common;

import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 分页数据封装类
 */
public class PageData extends PageBase implements Serializable {

    public static PageData data(PageInfo p) {
        PageData pd = new PageData();
        pd.setTotal(p.getTotal());
        pd.setCurrentPage(p.getPageNum());
        pd.setPageSize(p.getPageSize());
        pd.setVos(p.getList());
        return pd;
    }

    public static PageData emptyData() {
        return data(new PageInfo(new ArrayList()));
    }
}
