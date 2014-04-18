package com.qumoon.commons.web;

import com.google.common.collect.Lists;
import com.google.common.math.IntMath;

import org.apache.commons.collections.CollectionUtils;

import java.math.RoundingMode;
import java.util.List;

/**
 * @author kevin
 */
public class Page<T> {

  private int totalSize = 0;
  private int pageSize = 1;
  private int pageNum = 0;
  private List<T> items = Lists.newArrayList();

  private Page() {
  }

  private Page(int totalSize, int pageNum, int pageSize, List<T> items) {
    this.totalSize = totalSize;
    this.pageSize = pageSize;
    this.pageNum = pageNum;
    if (null != items) {
      this.items = items;
    }
  }

  public static <T> Page<T> empty() {
    return new Page<T>();
  }

  public static <T> Page<T> create(int totalSize, int pageNum, int pageSize, List<T> items) {
    return new Page(totalSize, pageNum, pageSize, items);
  }

  public int getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(int totalSize) {
    this.totalSize = totalSize;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
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
    return IntMath.divide(totalSize, pageSize, RoundingMode.CEILING);
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
    return (totalSize == 0) || (pageNum == getPages());
  }

  public boolean isEmpty() {
    return CollectionUtils.isEmpty(items);
  }
}
