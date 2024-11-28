package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.dto.response.DepResponseDTO;
import com.bitcamp.drrate.domain.products.dto.response.InsResponseDTO;
import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.service.ProductsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping(value="api/products")
@RequiredArgsConstructor
public class InsertProductController {
    private final ProductsService productsService;

    @Value("${api.depositUrl}")
    private String depositUrl;

    @Value("${api.savingUrl}")
    private String savingUrl;

    // 예금
    @GetMapping(value = "insertDep")
    public void insertDep() {
        insertProductData("d", depositUrl, true);  // 예금 (deposit)
    }

    // 적금
    @GetMapping(value = "insertIns")
    public void insertIns() {
        insertProductData("i", savingUrl, false);  // 적금 (installment)
    }

    // 상품 중복 확인
    public boolean isProductExists(String prdCo) {
        return productsService.existsByPrdCo(prdCo);
    }

    private void insertProductData(String category, String url, boolean isDeposit) {
        try {
            // API에서 데이터 가져오기
            BufferedReader bf = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "UTF-8"));
            String result = bf.readLine();

            // JSON 응답을 Java 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            ProductResponseDTO responseDTO = objectMapper.readValue(result, ProductResponseDTO.class);

            // 제품 리스트 추출
            List<ProductResponseDTO.ProductApiDto> productList = responseDTO.getResult().getBaseList();
            List<?> optionList = responseDTO.getResult().getOptionList();

            System.out.println("옵션 리스트: " + optionList);

            // 각 상품 엔티티 객체로 변환 후 DB에 저장
            for (ProductResponseDTO.ProductApiDto apiDto : productList) {

                if (isProductExists(apiDto.getPrdCo())) {
                    // 이미 존재하면 삽입하지 않음
                    continue;
                }

                // 기본 상품 저장
                Products product = new Products();
                product.setCtg(category);
                product.setPrdCo(apiDto.getPrdCo());
                product.setBankCo(apiDto.getBankCo());
                product.setBankName(apiDto.getBankName());
                product.setPrdName(apiDto.getPrdName());
                product.setJoinWay(apiDto.getJoinWay());
                product.setMtrtInt(apiDto.getMtrtInt());
                product.setSpclCnd(apiDto.getSpclCnd());
                product.setJoinMember(apiDto.getJoinMember());
                product.setEtc(apiDto.getEtc());
                product.setMax(apiDto.getMax());

                // 상품 저장
                productsService.loadAndSaveProducts(product);

                // 옵션 저장 (옵션 리스트가 null이 아니면)
                if (optionList != null && !optionList.isEmpty()) {

                    if (isDeposit) {
                        // 예금 옵션 처리
                        for (DepResponseDTO.OptionApiDto optionApiDto : (List<DepResponseDTO.OptionApiDto>) optionList) {
                            if (optionApiDto.getFinPrdtCd().equals(apiDto.getPrdCo())) {
                                DepositeOptions optionEntity = new DepositeOptions();
                                optionEntity.setRateType(optionApiDto.getIntrRateType());
                                optionEntity.setRateTypeKo(optionApiDto.getIntrRateTypeNm());
                                optionEntity.setSaveTime(Integer.parseInt(optionApiDto.getSaveTrm())); // saveTrm을 Integer로 변환
                                optionEntity.setBasicRate(optionApiDto.getBasicRate());
                                optionEntity.setSpclRate(optionApiDto.getSpclRate());

                                // `product`는 `Products` 엔티티
                                optionEntity.setProducts(product);

                                // 예금 옵션 엔티티를 저장
                                productsService.insertDep(optionEntity);
                            }
                        }
                    } else {
                        // 적금 옵션 처리
                        for (InsResponseDTO.OptionApiDto optionApiDto : (List<InsResponseDTO.OptionApiDto>) optionList) {
                            if (optionApiDto.getFinPrdtCd().equals(apiDto.getPrdCo())) {
                                InstallMentOptions optionEntity = new InstallMentOptions();
                                optionEntity.setRateType(optionApiDto.getIntrRateType());
                                optionEntity.setRateTypeKo(optionApiDto.getIntrRateTypeNm());
                                optionEntity.setRsrvType(optionApiDto.getRsrvType());
                                optionEntity.setRsrvTypeName(optionApiDto.getRsrvTypeNm());
                                optionEntity.setSaveTime(Integer.parseInt(optionApiDto.getSaveTrm())); // saveTrm을 Integer로 변환
                                optionEntity.setBasicRate(optionApiDto.getBasicRate());
                                optionEntity.setSpclRate(optionApiDto.getSpclRate());

                                // `product`는 `Products` 엔티티
                                optionEntity.setProducts(product);

                                // 적금 옵션 엔티티를 저장
                                productsService.insertIns(optionEntity);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리 (로깅 등)
        }
    }
}
