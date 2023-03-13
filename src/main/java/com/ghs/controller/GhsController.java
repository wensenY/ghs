package com.ghs.controller;

import com.ghs.entity.Thumbnail;
import com.ghs.service.Impl.BiGirlHtmlServiceImpl;
import com.ghs.service.Impl.WallhavenHtmlServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/ghs")
public class GhsController {

    @Resource
    private WallhavenHtmlServiceImpl wallhavenHtmlService;

    /**
     * 给缓存中加载数据
     */
    @GetMapping("/load")
    public void load(String type) {
        wallhavenHtmlService.getThumbnailsByTime();
    }

    /**
     * 随机获取一张图片
     */
    @GetMapping("/random")
    @ResponseBody
    public Thumbnail random() {
        int total = wallhavenHtmlService.getTotal();
        int random = (int) (Math.random() * total);
        return wallhavenHtmlService.getThumbnailByIndex(random);
    }

    //重定向到随机图片
    @GetMapping("/randomUrl")
    public RedirectView randomUrl() {
        return new RedirectView(random().getBigUrl());
    }

    @GetMapping("/getTotal")
    @ResponseBody
    public int getTotal() {
        return wallhavenHtmlService.getTotal();
    }





}
