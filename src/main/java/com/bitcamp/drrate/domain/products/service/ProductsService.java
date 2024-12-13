package com.bitcamp.drrate.domain.products.service;

import java.util.List;
import java.util.Map;

import com.bitcamp.drrate.domain.products.entity.Products;

public interface ProductsService {
    public Map<String, Object> getOneProduct(String prd_id);
    //241211 전체 조회 카테고리 조회 추가 - 오혜진 		
    //List<Products> getAllProducts(); 
    List<Map<String, Object>> getAllProducts(); // 전체 조회
    List<Products> getProductsByCtg(String ctg); //카테고리 조회 

}
