package com.ghs.Utils;

import com.ghs.entity.Thumbnail;
import com.ghs.redis.StringRedisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * html工具类
 */
@Component
@Slf4j
public class HtmlUtil {
    @Resource
    private StringRedisServiceImpl stringRedisService;
    
    /**
     * 获取html
     */
    public static String getHtml(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        urlcon.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream is = urlcon.getInputStream();
    
        byte[] b = new byte[1024];
        int len = 0;
        StringBuilder builder = new StringBuilder();
        while ((len = is.read(b)) != -1) {
            builder.append(new String(b, 0, len));
        }
        //关闭流
        is.close();
        //等待3秒
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
    
    public  int getTotal(String key) {
        return stringRedisService.getListKey(key).size();
    }
    
    /**
     * 添加缓存
     */
    public void addThumbnails(List<Thumbnail> thumbnails,String key) {
        for (Thumbnail thumbnail : thumbnails) {
            //判断是否存在 以前缀+url+总数为key
            Set<String> listKey = stringRedisService.getListKey(key + thumbnail.getUrl());
            if (listKey.isEmpty()) {
                //如果不存在就添加
                stringRedisService.set(key + thumbnail.getUrl()+ getTotal(key), thumbnail);
                log.info("添加缓存成功,缓存key为:{}", key + thumbnail.getUrl() + getTotal(key));
            }
            else {
                log.info("缓存已存在,自动跳过");
            }
        }
    }
    
}
