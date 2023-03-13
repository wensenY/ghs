package com.ghs.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * html工具类
 */
public  class HtmlUtil {
    
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
        return builder.toString();
    }
    
}
