package com.bitcamp.drrate.domain.products.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.s3.AmazonS3;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.ProductServiceExceptionHandler;
import org.hibernate.query.sqm.ParsingException;
import org.springframework.stereotype.Service;

import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.InstallMentOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.ProductsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService{
    private final ProductsRepository productsRepository;
    private final DepositeOptionsRepository depositeOptionsRepository;
    private final InstallMentOptionsRepository installMentOptionsRepository;

    private final AmazonS3 amazonS3;

    /* 상품 코드 확인 처리 */
    @Override
    public Long getPrdId(String prdId) {
        Long id = Long.parseLong(prdId);
        Products products = productsRepository.findById(id)
                .orElseThrow(() -> new ProductServiceExceptionHandler(ErrorStatus.PRD_ID_ERROR));
        return id;
    }



    /* 상품 출력 */
    @Override
    public Map<String, Object> getOneProduct(Long prdId) {
        Optional<Products> product;
        List<DepositeOptions> dep_options;
        List<InstallMentOptions> ins_options;

        Map<String, Object> map = new HashMap<>();

        // 상품 조회
        try {
            product = productsRepository.findById(prdId);
            if(!product.isPresent()){
                throw new ProductServiceExceptionHandler(ErrorStatus.PRODUCT_NOT_FOUND);
            }
        } catch (Exception e){
            throw new ProductServiceExceptionHandler(ErrorStatus.PRD_UNKNOWN_ERROR);
        }

        map.put("product", product.get());

        // 옵션 조회
        try{
            dep_options = depositeOptionsRepository.findByProductsId(prdId); // 예금
            ins_options = installMentOptionsRepository.findByProductsId(prdId); // 적금

            if ((dep_options == null || dep_options.isEmpty()) &&
                    (ins_options == null || ins_options.isEmpty())) {
                throw new ProductServiceExceptionHandler(ErrorStatus.OPTION_NOT_FOUND);
            }

        } catch (Exception e) {
            throw new ProductServiceExceptionHandler(ErrorStatus.PRD_UNKNOWN_ERROR);
        }

        if(dep_options != null && !dep_options.isEmpty()){
            map.put("options", dep_options);
        }else if(ins_options != null && !ins_options.isEmpty()){
            map.put("options", ins_options);
        }


        // 상품 우대 조건
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

                    try {
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

                    }catch (ClassCastException e){
                        throw new ProductServiceExceptionHandler(ErrorStatus.CONDITIONS_UNKNOWN_ERROR);
                    }
                }
            }
        } else {
            System.out.println("없음");
            specialConditions = null;
        }

        // 특수 조건을 파싱하여 ProdcutCondition 리스트로 변환
        List<ProductResponseDTO.ProductCondition> conditions;
        try{
            conditions = SpecialConditionsParser.parseSpecialConditions(specialConditions, basicRate, spclRate);
        } catch (ParsingException e) {
            throw new ProductServiceExceptionHandler(ErrorStatus.CONDITIONS_SPECIAL_PARSE_ERROR);
        } catch (Exception e) {
            throw new ProductServiceExceptionHandler(ErrorStatus.CONDITIONS_SPECIAL_UNKNOWN_ERROR);
        }

        map.put("conditions", conditions);
        map.put("optionNum", optionNum);

        return map;
    }


   //241211 오혜진 추가

    @Override
    public List<Map<String, Object>> getAllProducts() {
        List<Products> products = productsRepository.findAll();
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Products product : products) {
            Map<String, Object> productMap = new HashMap<>();
            Long productId = product.getId();

            // 각 상품의 옵션 조회
            List<DepositeOptions> dep_options = depositeOptionsRepository.findByProductsId(productId);
            List<InstallMentOptions> ins_options = installMentOptionsRepository.findByProductsId(productId);

            productMap.put("product", product);

            if (dep_options != null && !dep_options.isEmpty()) {
                productMap.put("options", dep_options);
            } else if (ins_options != null && !ins_options.isEmpty()) {
                productMap.put("options", ins_options);
            }
            
            

            resultList.add(productMap);
        }

        return resultList;
    }
    //241211 카테고리 - 오혜진
    @Override
    public List<Products> getProductsByCtg(String ctg) {
        return productsRepository.findByCtg(ctg);
    }
}
