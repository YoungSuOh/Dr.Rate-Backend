package com.bitcamp.drrate.domain.products.service;

import com.bitcamp.drrate.domain.products.dto.response.DepResponseDTO;
import com.bitcamp.drrate.domain.products.dto.response.InsResponseDTO;
import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.InstallMentOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.ProductsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InsertProductServiceImpl implements InsertProductService{
    private final ProductsRepository productsRepository;
    private final InstallMentOptionsRepository installMentOptionsRepository;
    private final DepositeOptionsRepository depositeOptionsRepository;

    // 상품 중복 확인
    public boolean isProductExists(String prdCo) {
        return productsRepository.existsByPrdCo(prdCo);
    }

    @Override
    public void insertProductData(String category, String url, boolean type) {
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
                productsRepository.save(product);

                // 옵션 저장 (옵션 리스트가 null이 아니면)
                if (optionList != null && !optionList.isEmpty()) {

                    if (type) {
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
                                depositeOptionsRepository.save(optionEntity);
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
                                installMentOptionsRepository.save(optionEntity);
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
