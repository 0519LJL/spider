package com.bbf.spider;

import lombok.Data;

@Data
public class Product {
    public int pid;//主键
    public String url;//地址
    public String text;//文字
    public boolean isMainSort =false;//主分类
    public String proName;//产品名
    public String brand;//品牌
    public String pinyin;//拼音
    public boolean ischufang = false;//是否处方
    public String proGuiGe;//规格
    public String projx;//剂型
    public String proUnit;//包装单位
    public String proPZWH;//批准文号
    public String proCode;//条形码
    public String proUsable;//主治疾病
    public String proMerchant;//生产厂家
    public int status = 1;//类型(0目录 1商品)
    public int proid;//目录节点


}
