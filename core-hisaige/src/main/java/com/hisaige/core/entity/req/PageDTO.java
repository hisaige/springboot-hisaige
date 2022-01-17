package com.hisaige.core.entity.req;

/**
 * @author chenyj
 * 2020/3/26 - 19:28.
 **/
public class PageDTO {
    private int currentPage = 1;
    private int pageSize = 25;

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

    @Override
    public String toString() {
        return "PageDTO{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}
