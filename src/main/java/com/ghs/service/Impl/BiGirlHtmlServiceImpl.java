package com.ghs.service.Impl;

import com.ghs.Utils.HtmlUtil;
import com.ghs.entity.Thumbnail;
import com.ghs.redis.StringRedisServiceImpl;
import com.ghs.service.HtmlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BiGirlHtmlServiceImpl implements HtmlService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisServiceImpl stringRedisService;
    @Resource
    private HtmlUtil httpUtil;
    
    public final String KEY = "Big-Girl";
    
    
    private static final String URL = "https://bi-girl.net/search-images/page/%d";
    
    
    @Override
    public String getKey() {
        return KEY;
    }
    
    @Override
    public void getThumbnailsByTime() {
        try {
            for (int i = 1; i <= 15; i++) {
                log.info("正在获取第{}页", i);
                List<Thumbnail> collect = parseUrl(String.format(URL, i));
                httpUtil.addThumbnails(collect, KEY);
                log.info("第{}页获取成功!", i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<Thumbnail> parseUrl(String urlStr) throws IOException {
        String html = HtmlUtil.getHtml(urlStr);
        log.info("解析首页html成功!");
        //处理html
        ArrayList<Thumbnail> thumbnails = new ArrayList<>();
        //获取缩略图地址
        
        
        //<a href = "https://pbs.twimg.com/media/FrBiBV7aYAMfikh.jpg" target = "_blank" rel = "noopener" class="img_a img_all" >
        //前缀
        String regex = "<a href=\"";
        //后缀
        String regex2 = "\" target=\"_blank\" rel=\"noopener\" class=\"img_a img_all\">";
    
        String[] split = html.split(regex);
        //检查split是否有后缀
        for (String s : split) {
            if (s.contains(regex2)) {
                String[] split1 = s.split(regex2);
                String url = split1[0];
                Thumbnail thumbnail = new Thumbnail();
                thumbnail.setBigUrl(url);
                thumbnail.setUrl(url);
                thumbnails.add(thumbnail);
            }
        }
        return thumbnails;
    }
    

    
    @Override
    public String parseDetailUrl(String url) throws IOException {
        return null;
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
