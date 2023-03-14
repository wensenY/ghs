package com.ghs.service.Impl;

import com.ghs.service.HtmlService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class HtmlServiceFactory {
    @Resource
    private WallhavenHtmlServiceImpl wallhavenHtmlService;
    @Resource
    private BiGirlHtmlServiceImpl biGirlHtmlService;
        
        public HtmlServiceFactory() {
        }
    
        public HtmlService getHtmlService(String type) {
            switch (type) {
                case "1":
                    return wallhavenHtmlService;
                case "2":
                    return biGirlHtmlService;
                default:
                    return null;
            }
        }
    
}
