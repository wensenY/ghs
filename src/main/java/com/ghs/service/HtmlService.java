package com.ghs.service;

import com.ghs.entity.Thumbnail;

import java.io.IOException;
import java.util.List;

public interface HtmlService {
    List<Thumbnail> getThumbnails();
    
    void getThumbnailsByTime();
    
    /**
     * 传入首页的url，返回首页的缩略图对象
     */
    List<Thumbnail> parseUrl(String url) throws IOException;
    
    /**
     * 解析首页Html的方法 传入的是首页的Html字符串 返回的是首页的缩略图对象
     */
    List<Thumbnail> parseHtml(String html) throws IOException;
    
    /**
     * 传入详情页的url，返回详情页的大图url
     */
    String parseDetailUrl(String url) throws IOException;
    
    /**
     * 解析详情页Html的方法 传入的是详情页的Html字符串 返回的是详情页的大图url
     */
    String parseDetailHtml(String html) throws IOException;
}
