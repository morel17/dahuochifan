package com.dahuochifan.model.discount;

import java.util.List;

/**
 * Created by admin on 2015/11/10.
 */
public class DiscountPage {
    private boolean lastPage;
    private int pageSize;
    private int pageNumber;
    private boolean firstPage;
    private List<DiscountDetail> list;

    public boolean isLastPage() {
        return lastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isFirstPage() {
        return firstPage;
    }

    public void setFirstPage(boolean firstPage) {
        this.firstPage = firstPage;
    }

    public List<DiscountDetail> getList() {
        return list;
    }

    public void setList(List<DiscountDetail> list) {
        this.list = list;
    }
}
