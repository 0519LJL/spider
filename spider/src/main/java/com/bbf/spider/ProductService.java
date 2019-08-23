package com.bbf.spider;

import java.util.List;

public interface ProductService {

    Product insert(Product product);

    void addMainSort() throws Exception;

    List<Product> selectMainSort();

    void getProductByMain() throws Exception;
}
