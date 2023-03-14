package com.ghs.service.Impl;

import com.ghs.Utils.HtmlUtil;
import com.ghs.entity.Thumbnail;
import com.ghs.redis.StringRedisServiceImpl;
import com.ghs.service.HtmlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WallhavenHtmlServiceImpl implements HtmlService {
    
    //redis
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisServiceImpl stringRedisService;
    @Resource
    private HtmlUtil httpUtil;
    
    public final String KEY = "wallhaven-";
    
    
    private static final String URL = "https://wallhaven.cc/search?categories=111&purity=010&sorting=hot&order=desc&page=%d";
    
    
    @Override
    public String getKey() {
        return KEY;
    }
    
    /**
     * 定时获取图片缓存 每天凌晨0点和12点
     */
    @Scheduled(cron = "0 0 0,12 * * ?")
    @Override
    public void getThumbnailsByTime() {
        try {
            for (int i = 1; i <= 15; i++) {
                log.info("正在获取第{}页", i);
                List<Thumbnail> collect = parseUrl(String.format(URL, i)).stream().map(thumbnail -> {
                    try {
                        thumbnail.setBigUrl(parseDetailUrl(thumbnail.getDetailUrl()));
                        return thumbnail;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
                httpUtil.addThumbnails(collect, KEY);
                log.info("第{}页获取成功!", i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * 传入首页的url，返回图片对象 带有缩略图地址和详情页地址
     */
    @Override
    public List<Thumbnail> parseUrl(String urlStr) throws IOException {
        String html = HtmlUtil.getHtml(urlStr);
        log.info("解析首页html成功!");
        //处理html
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
    
    /**
     * 传入详情页的url，返回图片对象 带有大图地址
     */
    @Override
    public String parseDetailUrl(String url) throws IOException {
        String html = HtmlUtil.getHtml(url);
        log.info("解析详情页html成功!");
        //处理html
        //获取图片地址,匹配截取字符串
        String[] split = html.split("<img id=\"wallpaper\" src=\"");
        String[] split1 = split[1].split("\"");
        return split1[0];
    }
    
    @Override
    public Thumbnail getThumbnailByIndex(int index) {
        Set<String> listKey = stringRedisService.getListKey(KEY);
        //查询是否以index结尾的key
        for (String s : listKey) {
            if (s.endsWith(String.valueOf(index))) {
                return (Thumbnail) stringRedisService.get(s);
            }
        }
        return null;
    }
}
