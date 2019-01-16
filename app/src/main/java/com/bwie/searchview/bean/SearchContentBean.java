package com.bwie.searchview.bean;

/**
 * date:2019/1/12
 * author:张自力(DELL)
 * function:  搜索的封装类
 */

public class SearchContentBean {

    private String searchString;

    public SearchContentBean() {
    }

    public SearchContentBean(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public String toString() {
        return "SearchContentBean{" +
                "searchString='" + searchString + '\'' +
                '}';
    }
}
