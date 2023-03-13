package com.ghs.service.Impl;

import com.ghs.entity.Thumbnail;
import com.ghs.service.HtmlService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BiGirlHtmlServiceImpl implements HtmlService {
    
    @Override
    public List<Thumbnail> getThumbnails() {
        return null;
    }
    
    @Override
    public void getThumbnailsByTime() {
    
    }
    
    @Override
    public List<Thumbnail> parseUrl(String html) {
        return null;
    }
    

    
    @Override
    public String parseDetailUrl(String url) throws IOException {
        return null;
    }

}
