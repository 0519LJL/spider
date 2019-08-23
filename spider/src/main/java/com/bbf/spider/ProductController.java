package com.bbf.spider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private  ProductService productService;

    @RequestMapping(value = "/index.html", method = RequestMethod.GET)
    public String addProduct() throws Exception {
//        Product product =  new Product();
//        product.proName = "ceshi";
//        product.ischufang = false;
//        productService.insert(product);
        productService.addMainSort();
        System.out.println("爬取成功");
        return "插入成功";
    }

    @RequestMapping(value = "/getMain.html", method = RequestMethod.GET)
    @ResponseBody
    public List<Product> getMainSort() throws Exception {
        System.out.println("爬取成功");

        return productService.selectMainSort();
    }

    @RequestMapping(value = "/getProductByMain.html", method = RequestMethod.GET)
    public void getProductByMain() throws Exception {
        Long start = System.currentTimeMillis();
        System.out.println("开始抓取药品,当前时间:" + start);
        productService.getProductByMain();
        Long end = System.currentTimeMillis();
        System.out.println("爬取完成,耗时:"+(end-start));
    }
}
