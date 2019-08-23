package com.bbf.spider;

import com.bbf.spider.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product insert(Product product){
        productMapper.insert(product);
        return product;
    }

    @Override
    public List<Product> selectMainSort(){
        return productMapper.selectMainSort();
    }

    @Override
    public void addMainSort() throws Exception {
        List<Product> productList = HTMLPageParser.getHeadStructure(HTMLPageParser.url);

        Product product = new Product();
        productList.forEach(menu ->{
            menu.status = 0;
            menu.proName = menu.text;
            productMapper.insert(menu);
        });
    }

    @Override
    public void getProductByMain() throws Exception {
        List<Product> productList =productMapper.selectMainSort();
        for (Product d : productList){
            String mainSortUrl = HTMLPageParser.url + HTMLPageParser.toUpperCaseFirstOne(d.url);
            try {
                System.out.println(mainSortUrl + "  page:" + HTMLPageParser.getProductListPage(mainSortUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                int page = HTMLPageParser.getProductListPage(mainSortUrl);
                List<Product> productUrlList = new ArrayList<>();
                String pageUrl = "";
                for (int i = 1; i <= page; i++) {
                    pageUrl = String.format(mainSortUrl + "defaultp%d.htm", i);
                    if (i == 1) {
                        pageUrl = String.format(mainSortUrl + "default.htm");
                    }
                    System.out.println("开始爬取:" + pageUrl);
                    productUrlList = (List<Product>) HTMLPageParser.getProUrl(pageUrl);
                    System.out.println(productUrlList);
                    productUrlList.forEach(productUrl ->{

                        try {
                            Product product =HTMLPageParser.getProductInfo(productUrl.url);
                            product.status = 1;
                            product.status = 1;
                            product.proid = d.pid;
                            productMapper.insert(product);
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
}
