package com.bitcamp.drrate.domain.products.service;

import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;

import java.util.Map;
import java.util.Optional;

public interface ProductsService {


    public Map<String, Object> getOneProduct(String prd_id);


    /* 상품 삽입 */
    //중복 확인
    public boolean existsByPrdCo(String prdCo);

    //상품 저장
    public void loadAndSaveProducts(Products product);

    //옵션 저장
    public void insertDep(DepositeOptions optionEntity);

    public void insertIns(InstallMentOptions insOptionEntity);
}
