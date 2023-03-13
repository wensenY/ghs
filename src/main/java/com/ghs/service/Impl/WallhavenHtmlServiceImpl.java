package com.ghs.service.Impl;

import com.ghs.Utils.HtmlUtil;
import com.ghs.entity.Thumbnail;
import com.ghs.service.HtmlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WallhavenHtmlServiceImpl implements HtmlService {
    
    private static final String WALLHAVEN_URL = "https://wallhaven.cc/search?categories=011&purity=010&topRange=1d&sorting=hot&order=desc&page=%d";
    
    //缓存对象
    private static List<Thumbnail> thumbnails = new ArrayList<>();
    
    @Override
    public  List<Thumbnail> getThumbnails() {
        return thumbnails;
    }
    
    /**
     * 定时获取图片缓存 1小时一次
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    @Override
    public void getThumbnailsByTime() {
        try {
            for (int i = 1; i <= 10; i++){
                List<Thumbnail> collect = parseUrl(String.format(WALLHAVEN_URL, i)).stream().map(thumbnail -> {
                    try {
                        thumbnail.setBigUrl(parseDetailUrl(thumbnail.getDetailUrl()));
                        return thumbnail;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
                addThumbnails(collect);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 添加缓存
     */
    private void addThumbnails(List<Thumbnail> thumbnails) {
        //检查是否有重复的
        for (Thumbnail thumbnail : thumbnails) {
            if (!WallhavenHtmlServiceImpl.thumbnails.contains(thumbnail)) {
                WallhavenHtmlServiceImpl.thumbnails.add(thumbnail);
            }
        }
    }
    
    @Override
    public List<Thumbnail> parseUrl(String urlStr) throws IOException {
        String html = HtmlUtil.getHtml(urlStr);
        log.info("解析首页html成功!");
        //处理html
        return parseHtml(html);
    }
    
    @Override
    public List<Thumbnail> parseHtml(String html) throws IOException {
        
        ArrayList<Thumbnail> thumbnails = new ArrayList<>();
        //获取缩略图地址
        String[] split = html.split("data-src=\"");
        String[] split1 = Arrays.copyOfRange(split, 1, split.length);
        for (String s : split1) {
            String[] split2 = s.split("\"");
            thumbnails.add(Thumbnail.builder().url(split2[0]).build());
        }
        
        //获取详情页地址
        String[] split2 = html.split("<a class=\"preview\" href=\"");
        ArrayList<String> detailUrl = new ArrayList<>();
        String[] split3 = Arrays.copyOfRange(split2, 1, split2.length);
        for (String s : split3) {
            String[] split4 = s.split("\"");
            detailUrl.add(split4[0]);
        }
        thumbnails.forEach(thumbnail -> thumbnail.setDetailUrl(detailUrl.get(thumbnails.indexOf(thumbnail))));
        return thumbnails;
    }
    
    @Override
    public String parseDetailUrl(String url) throws IOException {
        String html = HtmlUtil.getHtml(url);
        log.info("解析详情页html成功!");
        //处理html
        return parseDetailHtml(html);
    }
    
    @Override
    public String parseDetailHtml(String html) throws IOException {
        //获取图片地址,匹配截取字符串
        String[] split = html.split("<img id=\"wallpaper\" src=\"");
        String[] split1 = split[1].split("\"");
        return split1[0];
    }
    
    
}
