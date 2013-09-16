package com.trilemon.commons.web;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author kevin
 */
public class Page<T> {
    private int totalSize = 0;
    private int size = 1;
    private int pageNum = 1;
    private List<T> items = Lists.newArrayList();

    private Page() {
    }

    public Page(int totalSize, int pageNum, int size, List<T> items) {
        this.totalSize = totalSize;
        this.size = size;
        this.pageNum = pageNum;
        this.items = items;
    }

    public static <T> Page<T> empty() {
        return new Page<T>();
    }

    public static void main(String[] args) {
        Page<String> page = Page.empty();
        page.setSize(2);
        page.setTotalSize(10);
        System.out.print(page.getPages());
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getPages() {
        int totalPages = totalSize / size;
        if (totalPages <= 1) {
            return 1;
        } else {
            return totalPages + (totalSize % size > 0 ? 1 : 0);
        }
    }

    public boolean hasFirstPage() {
        return pageNum > 1;
    }

    public boolean hasLastPage() {
        return getPages() > 1 && pageNum < getPages();
    }

    public boolean hasPrePage() {
        return getPages() > 1 && pageNum > 1;
    }

    public boolean hasNextPage() {
        return getPages() > 1 && pageNum < getPages();
    }

    public boolean isLastPage() {
        return pageNum == getPages();
    }
}
