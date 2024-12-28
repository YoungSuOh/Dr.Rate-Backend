package com.bitcamp.drrate.domain.products.service;

import java.util.List;
import java.util.Map;

import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.Products;

public interface ProductsService {
    /* 상품 코드 확인 */
    Long getPrdId(String prdId);

    /* 상품 하나 출력 */
    Map<String, Object> getOneProduct(Long prdId);

    List<Products> getProductsByCtg(String ctg); //카테고리 조회

    List<ProductResponseDTO.ProductListDTO>getGuestProduct(Integer page, Integer size, String category, String[] banks, String sort);

    List<ProductResponseDTO.ProductListDTO>getProduct(Integer page, Integer size, String category, String[] banks, Integer age, Integer period, String rate, String join, String sort);
}
