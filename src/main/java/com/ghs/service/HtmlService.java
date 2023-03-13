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
     * 传入详情页的url，返回详情页的大图url
     */
    String parseDetailUrl(String url) throws IOException;

}
