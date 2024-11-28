package com.bitcamp.drrate.domain.products.service;

import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.InstallMentOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.ProductsRepository;
import com.bitcamp.drrate.domain.products.service.parser.SpecialConditionsParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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


        // 상품
        Optional<Products> optionalProduct = (product);
        String specialConditions;

        BigDecimal basicRate = BigDecimal.ZERO;
        BigDecimal spclRate = BigDecimal.ZERO;
        int optionNum = 0;

        if (optionalProduct.isPresent()) {
            Products getProduct = optionalProduct.get();
            specialConditions = getProduct.getSpclCnd();

            // 옵션 리스트
            List<?> options = (List<?>) map.get("options");

            // 옵션이 있을 경우
            if (options != null && !options.isEmpty()) {

                // 옵션 테이블 종류 구별
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i) instanceof DepositeOptions) {
                        DepositeOptions option = (DepositeOptions) options.get(i);

                        // 옵션 중 우대금리 포함 금리 가장 높은걸로 선별
                        if (spclRate.compareTo(option.getSpclRate()) < 0) {
                            spclRate = option.getSpclRate();
                            basicRate = option.getBasicRate();
                            optionNum = i;
                        }

                    } else if (options.get(i) instanceof InstallMentOptions) {
                        InstallMentOptions option = (InstallMentOptions) options.get(i);

                        if (spclRate.compareTo(option.getSpclRate()) < 0) {
                            spclRate = option.getSpclRate();
                            basicRate = option.getBasicRate();
                            optionNum = i;
                        }
                    }
                }
            }
        } else {
            System.out.println("없음");
            specialConditions = null;
        }

        // 특수 조건을 파싱하여 ProdcutCondition 리스트로 변환
        List<ProductResponseDTO.ProductCondition> conditions = SpecialConditionsParser.parseSpecialConditions(specialConditions, basicRate, spclRate);

        map.put("conditions", conditions);
        map.put("optionNum", optionNum);

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
