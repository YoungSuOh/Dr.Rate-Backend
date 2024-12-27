package com.bitcamp.drrate.domain.products.service;

import java.util.List;
import java.util.Map;

import com.bitcamp.drrate.domain.products.entity.Products;

public interface ProductsService {
    /* 상품 코드 확인 */
    Long getPrdId(String prdId);

    /* 상품 하나 출력 */
    Map<String, Object> getOneProduct(Long prdId);

    List<Products> getProductsByCtg(String ctg); //카테고리 조회

    List<Map<String, Object>>getProduct(int page, int size, String bank, int age, int period, String rate, String join, boolean spclRate, boolean basicRate);
}
