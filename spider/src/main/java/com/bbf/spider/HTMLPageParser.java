package com.bbf.spider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLPageParser {
    public static String url = "https://www.315jiage.cn/";
    public static void main(String[] args) throws Exception {
        //目的网页URL地址
        List<Product> productList = getHeadStructure(url);
        System.out.println(productList);
        AtomicReference<Product> productDetail = new AtomicReference<>(new Product());
        productList.forEach(product -> {
            if (product.isMainSort) {
                String mainSortUrl = url + toUpperCaseFirstOne(product.url);
                try {
                    System.out.println(mainSortUrl + "  page:" + getProductListPage(mainSortUrl));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (product.url.contains("XiaoShaYongPin")) {
                    try {
                        int page = getProductListPage(mainSortUrl);
                        List<Product> productUrlList = new ArrayList<>();
                        String pageUrl = "";
                        for (int i = 1; i <= page; i++) {
                            pageUrl = String.format(mainSortUrl + "defaultp%d.htm", i);
                            if (i == 1) {
                                pageUrl = String.format(mainSortUrl + "default.htm");
                            }
                            System.out.println("开始爬取:" + pageUrl);
                            productUrlList = getProUrl(pageUrl);
                            System.out.println(productUrlList);
                            productUrlList.forEach(productUrl ->{

                                try {
                                    System.out.println(getProductInfo(productUrl.url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        System.out.println("爬取成功");
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static String getURLInfo(String urlInfo) throws Exception {
        //读取目的网页URL地址，获取网页源码
        URL url = new URL(urlInfo);
        HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
        httpUrl.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"); //防止报403错误
        InputStream is = httpUrl.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        br.close();
//        System.out.println(sb.toString().trim());
        //获得网页源码
        return sb.toString().trim();
    }


    //获取首页目录
    public static List<Product> getHeadStructure(String url) throws Exception {

        String mainSort = "";
        String str = getURLInfo(url);
        List<Product> list = new ArrayList<Product>();
        Pattern proInfo = Pattern.compile("<div class=\"bd top-sorts\">(.*?)</div>");
        Matcher matcher_a = proInfo.matcher(str);
        List<String> urlList = new ArrayList<>();

        String pageInfor = "";
        while (matcher_a.find()) {
            pageInfor = matcher_a.group(1);
        }

        proInfo = Pattern.compile("<a[^>]*href=(\"([^\"]*)\")[^>]*>(.*?)</a>");
        matcher_a = proInfo.matcher(pageInfor);
        Product product = null;
        while (matcher_a.find()) {
            product = new Product();
            product.url = matcher_a.group(2);
            product.text = matcher_a.group(3);
            if (matcher_a.group(0).toString().contains("mainsort")) {
                product.isMainSort = true;
                mainSort = matcher_a.group(3);
            } else {
                product.text = mainSort + "-" + matcher_a.group(3);
            }
            list.add(product);
//            System.out.println(product);
        }
        return list;
    }

    //获取所有商品地址
    public static List<Product> getProUrl(String url) throws Exception {
        String str = getURLInfo(url);

        Pattern proInfo = Pattern.compile("<div class=\"sCard\">(.*?)</div>");
        Matcher matcher_a = proInfo.matcher(str);
        List<String> urlList = new ArrayList<>();
        while (matcher_a.find()) {
            urlList.add(matcher_a.group(1));
        }


        List<Product> list = new ArrayList<Product>();
        Product product = null;

        Pattern proInfo1 = Pattern.compile("<a[^>]*href=(\"([^\"]*)\")[^>]*>(.*?)</a>", Pattern.DOTALL);
        for (String proUrl : urlList) {
            Matcher matcher_a1 = proInfo1.matcher(proUrl);
            while (matcher_a1.find()) {
                product = new Product();

                product.url = matcher_a1.group(2).toString().replace("..", "https://www.315jiage.cn");
                product.text = matcher_a1.group(3);
//                System.out.println(product.toString());
                list.add(product);
            }
        }


        return list;
    }

    //获取分类最大页
    static int getProductListPage(String url) throws Exception {
        int page = 0;
        String pageInfo = "";
        String info = getURLInfo(url);

        //获取分页内容
        Pattern proInfo = Pattern.compile("<div class=\"pager\">(.*?)</div>");
        Matcher matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            pageInfo = matcher_a.group(1);
        }

        proInfo = Pattern.compile("<a[^>]*href=(\"([^\"]*)\")[^>]*>(.*?)</a>");
        matcher_a = proInfo.matcher(pageInfo);
        while (matcher_a.find()) {
            try {
                page = Integer.parseInt(matcher_a.group(3));
            } catch (Exception ex) {

            }
        }
        return page;
    }

    //获取内容
    public static Product getProductInfo(String url) throws Exception {
        String str = getURLInfo(url);

        Pattern proInfo = Pattern.compile("<div class=\"main clearfix\">(.*?)></div>");
        Matcher matcher_a = proInfo.matcher(str);
        String productInfo = "";

        while (matcher_a.find()) {
            productInfo = matcher_a.group(0);
        }


        Product product = new Product();
        proInfo = Pattern.compile("<p>(.*?)</p>");
        matcher_a = proInfo.matcher(productInfo);
        while (matcher_a.find()) {

//            System.out.println(matcher_a.group(1));

            product.url = url;
            if(matcher_a.group(1).contains("名称")){
               HashMap productMap = getProduct(matcher_a.group(1));
               product.proName = productMap.get("name").toString();
               product.brand = productMap.get("brand").toString();
               product.pinyin = productMap.get("pinyin").toString();
            }
            if(matcher_a.group(1).contains("规格")){
                HashMap ggMap = getGuige(matcher_a.group(1));
                product.proGuiGe = ggMap.get("gg").toString();
                product.projx = ggMap.get("jx").toString();
                product.proUnit = ggMap.get("unit").toString();
            }

            if(matcher_a.group(1).contains("批准文号")){
                HashMap pzwhMap = getPzwh(matcher_a.group(1));
                product.proPZWH = pzwhMap.get("pzwh").toString();
                product.ischufang =  Boolean.parseBoolean(pzwhMap.get("ischufang").toString());
            }
            if(matcher_a.group(1).contains("条形码")){
                product.proCode = getQrCode(matcher_a.group(1));
            }
            if(matcher_a.group(1).contains("生产厂家")){
                product.proMerchant = getMerchant(matcher_a.group(1));
            }
            if(matcher_a.group(1).contains("主治疾病")){
                product.proUsable = getZZ(matcher_a.group(1));
            }
        }
//        System.out.println(product);
        return product;
    }

    private static HashMap getProduct(String info){
        HashMap productMap = new HashMap<String,String>();
        productMap.put("name","");
        productMap.put("brand","");
        productMap.put("pinyin","");

        Pattern proInfo = Pattern.compile("<span itemprop=\"name\"><u>(.*?)</u>");
        Matcher matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            productMap.put("name",matcher_a.group(1));
        }
        proInfo = Pattern.compile("</u>.*\\((.*?)\\)</span>");
        matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            productMap.put("brand",matcher_a.group(1));
        }

        proInfo = Pattern.compile("</span>.*?<u>(.*?)</u>");
        matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            productMap.put("pinyin",matcher_a.group(1));
        }
        return  productMap;
    }

    private static HashMap getGuige(String info){
        HashMap<String,String> ggMap = new HashMap<>();
        ggMap.put("gg","");
        ggMap.put("jx","");
        ggMap.put("unit","");

        Pattern proInfo = Pattern.compile("<u>(.*?)</u>　剂型");
        Matcher matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            ggMap.put("gg",matcher_a.group(1).toString().replace("</u> <u>"," "));
        }

        proInfo = Pattern.compile("剂型：(.*?)包装单位");
        matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            ggMap.put("jx",(matcher_a.group(1) == null ? "":matcher_a.group(1).toString().trim()));
        }

        proInfo = Pattern.compile("包装单位：<u>(.*?)</u>");
        matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            ggMap.put("unit",matcher_a.group(1).toString().trim());
        }
        return ggMap;
    }

    private static HashMap getPzwh(String info){

        HashMap<String,String> pzMap = new HashMap<>();
        pzMap.put("ischufang","false");
        pzMap.put("pzwh","");

        Pattern proInfo = Pattern.compile("<a.*?>(.*?)</a>");
        Matcher matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            pzMap.put("pzwh",(matcher_a.group(1) == null ? "":matcher_a.group(1)));
        }

        proInfo = Pattern.compile("<span class=\"cRed\">(.*?)</span>");
        matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
            pzMap.put("ischufang","true");
        }

        return pzMap;
    }

    private static String getMerchant(String info){
        Pattern proInfo = Pattern.compile("<u>(.*?)</u>");
        Matcher matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
//            System.out.println("生产厂家:" + matcher_a.group(1));
            return  matcher_a.group(1);
        }
        return "";
    }

    private static String getQrCode(String info){
        Pattern proInfo = Pattern.compile("<div id=\"content\"(.*?)</p></div>");
        proInfo = Pattern.compile("(?<=条形码：)\\d+");
        Matcher matcher_a = proInfo.matcher(info);
        while (matcher_a.find()) {
           return matcher_a.group(0);
        }
        return "";
    }

    //获取主治主治疾病
    private static String getZZ(String info){
        String[] strs = info.replace("&nbsp;","、").split("：");
        return strs[1];
    }
}


