package com.bbf.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebPageSource {
    public static void main(String args[]) throws Exception{
        URL url;
        int responsecode;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String line;
        try {
            //生成一个URL对象，要获取源代码的网页地址为：http://www.sina.com.cn
            url = new URL("https://www.315jiage.cn/x-GanMao/298920.htm");
            //打开URL
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"); //防止报403错误。
            //获取服务器响应代码
            responsecode = urlConnection.getResponseCode();
            if (responsecode == 200) {
                //得到输入流，即获得了网页的内容 
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

            } else {
                System.out.println("获取不到网页的源码，服务器响应代码为：" + responsecode);
            }
        } catch (Exception e) {
            System.out.println("获取不到网页的源码,出现异常：" + e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
    }
}
