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
import java.util.stream.Collectors;

@Service
@Slf4j
public class WallhavenHtmlServiceImpl implements HtmlService {

    //redis
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisServiceImpl stringRedisService;

    private final String key = "wallhaven-";

    public int getTotal() {
        return stringRedisService.getListKey(key).size();
    }

    private static final String WALLHAVEN_URL = "https://wallhaven.cc/search?categories=011&purity=010&topRange=1d&sorting=hot&order=desc&page=%d";

    //缓存对象
    private static List<Thumbnail> thumbnails = new ArrayList<>();


    @Override
    public List<Thumbnail> getThumbnails() {
        return thumbnails;
    }

    /**
     * 定时获取图片缓存 1小时一次
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    @Override
    public void getThumbnailsByTime() {
        try {
            for (int i = 1; i <= 10; i++) {
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
        for (Thumbnail thumbnail : thumbnails) {
            if (!stringRedisService.hasKey(key + thumbnail.getUrl())) {
                //如果不存在就添加
                stringRedisService.set(key+getTotal(), thumbnail);
                System.out.println("添加缓存成功! " + thumbnail.getUrl());
            }
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
        //处理html
        //获取图片地址,匹配截取字符串
        String[] split = html.split("<img id=\"wallpaper\" src=\"");
        String[] split1 = split[1].split("\"");
        System.out.println(split1[0]);
        //睡眠1秒
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return split1[0];
    }


    public Thumbnail getThumbnailByIndex(int random) {
        Object o = stringRedisService.get(key + random);
        if (o == null) {
            return null;
        }
        return (Thumbnail) o;
    }
}
