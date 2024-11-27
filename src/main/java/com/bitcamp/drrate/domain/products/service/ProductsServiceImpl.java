package com.bitcamp.drrate.domain.products.service;

import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.InstallMentOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService{
    private final ProductsRepository productsRepository;
    private final DepositeOptionsRepository depositeOptionsRepository;
    private final InstallMentOptionsRepository installMentOptionsRepository;

    //상품 출력
    @Override
    public Map<String, Object> getOneProduct(String prd_id) {
        Optional<Products> product = productsRepository.findById(Long.parseLong(prd_id));

        List<DepositeOptions> dep_options = depositeOptionsRepository.findByProductsId(Long.parseLong(prd_id));
        List<InstallMentOptions> ins_options = installMentOptionsRepository.findByProductsId(Long.parseLong(prd_id));

        Map<String, Object> map = new HashMap<>();
        map.put("product", product);

        if(dep_options != null && !dep_options.isEmpty()){
            map.put("options", dep_options);
        }else if(ins_options != null && !ins_options.isEmpty()){
            map.put("options", ins_options);
        }

        return map;
    }


    /* 상품 삽입 */
    // 중복확인
    @Override
    public boolean existsByPrdCo(String prdCo) {
        return productsRepository.existsByPrdCo(prdCo);
    }

    // 상품 저장
    @Override
    public void loadAndSaveProducts(Products product) {
        productsRepository.save(product);
    }

    // 옵션 저장
    @Override
    public void insertDep(DepositeOptions optionEntity) {
        depositeOptionsRepository.save(optionEntity);
    }

    @Override
    public void insertIns(InstallMentOptions insOptionEntity) {
        installMentOptionsRepository.save(insOptionEntity);
    }

}
