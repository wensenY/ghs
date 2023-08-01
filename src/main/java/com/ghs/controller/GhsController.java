package com.ghs.controller;

import com.ghs.Utils.HtmlUtil;
import com.ghs.entity.Thumbnail;
import com.ghs.redis.StringRedisServiceImpl;
import com.ghs.service.HtmlService;
import com.ghs.service.Impl.HtmlServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/pc")
public class GhsController {
    @Resource
    private HtmlUtil htmlUtil;
    @Resource
    private HtmlServiceFactory htmlServiceFactory;
    @Resource
    private StringRedisServiceImpl stringRedisService;

    /**
     * 给缓存中加载数据
     */
    @GetMapping("/load")
    @ResponseBody
    public void load(@RequestParam(defaultValue = "1") String type) {
        htmlServiceFactory.getHtmlService(type).getThumbnailsByTime();
    }

    /**
     * 随机获取一张图片
     */
    @GetMapping("/random")
    @ResponseBody
    public Thumbnail random(@RequestParam(defaultValue = "1") String type) {
        HtmlService htmlService = htmlServiceFactory.getHtmlService(type);
        Boolean hasKey = true;
        Thumbnail thumbnail = null;
        int total = htmlUtil.getTotal(htmlService.getKey());
        do {
            int random = (int) (Math.random() * total);
            thumbnail = htmlService.getThumbnailByIndex(random);
            //检查是否已经存在redis中
            hasKey = stringRedisService.hasKey("thumbnail" + thumbnail.getUrl());
        } while (hasKey);   //如果存在则重新获取
        //存入redis 过期时间为1天
        stringRedisService.set("thumbnail" + thumbnail.getUrl(), "", 60 * 60 * 24);
        return thumbnail;
    }

    //重定向到随机图片
    @GetMapping("/getImg")
    public RedirectView randomUrl(@RequestParam(defaultValue = "1") String type) {
        return new RedirectView(random(type).getBigUrl());
    }

    @GetMapping("/getTotal")
    @ResponseBody
    public int getTotal(@RequestParam(defaultValue = "1") String type) {
        String key = htmlServiceFactory.getHtmlService(type).getKey();
        return htmlUtil.getTotal(key);
    }
}

