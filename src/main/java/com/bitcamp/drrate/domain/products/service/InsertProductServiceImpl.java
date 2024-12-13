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
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.ProductServiceExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
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
            ProductResponseDTO responseDTO;
            DepResponseDTO depResponseDTO;
            InsResponseDTO insResponseDTO;

            try {
                responseDTO = objectMapper.readValue(result, ProductResponseDTO.class);
                depResponseDTO = objectMapper.readValue(result, DepResponseDTO.class);
                insResponseDTO = objectMapper.readValue(result, InsResponseDTO.class);
            } catch (JsonProcessingException e) {
                throw new ProductServiceExceptionHandler(ErrorStatus.JSON_PARSING_ERROR);
            }

            // 제품 리스트 추출
            List<ProductResponseDTO.ProductApiDto> productList = responseDTO.getResult().getBaseList();
            List<?> optionList = type ? depResponseDTO.getResult().getOptionList() : insResponseDTO.getResult().getOptionList();

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

                String bankName = apiDto.getBankName();
                String logo = "remainLogo.png";  // 기본값

                if ("하나은행".equals(bankName)) {
                    logo = "hanaLogo.png";
                } else if ("주식회사 카카오뱅크".equals(bankName)) {
                    logo = "kakaoLogo.png";
                } else if ("국민은행".equals(bankName)) {
                    logo = "kookminLogo.png";
                } else if ("농협은행주식회사".equals(bankName)) {
                    logo = "nonghyupLogo.png";
                } else if ("신한은행".equals(bankName)) {
                    logo = "shinhanLogo.png";
                } else if ("토스뱅크 주식회사".equals(bankName)) {
                    logo = "tossLogo.png";
                } else if ("우리은행".equals(bankName)) {
                    logo = "wooriLogo.png";
                }

                product.setBankLogo(logo);

                // 상품 저장
                try {
                    productsRepository.save(product);
                } catch (Exception e) {
                    throw new ProductServiceExceptionHandler(ErrorStatus.INSERT_PRD_ERROR);
                }

                // 옵션 저장 (옵션 리스트가 null이 아니면)
                if (optionList != null && !optionList.isEmpty()) {

                    if (type) {
                        // 예금 옵션 처리
                        for (DepResponseDTO.OptionApiDto optionApiDto : (List<DepResponseDTO.OptionApiDto>) optionList) {
                            try {
                                if (optionApiDto.getFinPrdtCd().equals(apiDto.getPrdCo())) {
                                    DepositeOptions optionEntity = new DepositeOptions();
                                    optionEntity.setRateType(optionApiDto.getIntrRateType());
                                    optionEntity.setRateTypeKo(optionApiDto.getIntrRateTypeNm());
                                    optionEntity.setSaveTime(Integer.parseInt(optionApiDto.getSaveTrm())); // saveTrm을 Integer로 변환
                                    optionEntity.setBasicRate(optionApiDto.getBasicRate());
                                    optionEntity.setSpclRate(optionApiDto.getSpclRate());

                                    // Products 엔티티에 저장
                                    optionEntity.setProducts(product);

                                    // 예금 옵션 엔티티를 저장
                                    depositeOptionsRepository.save(optionEntity);
                                }

                            } catch (Exception e) {
                                throw new ProductServiceExceptionHandler(ErrorStatus.INSERT_DEPOPTIONS_ERROR);
                            }
                        }
                    } else {
                        // 적금 옵션 처리
                        for (InsResponseDTO.OptionApiDto optionApiDto : (List<InsResponseDTO.OptionApiDto>) optionList) {
                            try{
                                if (optionApiDto.getFinPrdtCd().equals(apiDto.getPrdCo())) {
                                    InstallMentOptions optionEntity = new InstallMentOptions();
                                    optionEntity.setRateType(optionApiDto.getIntrRateType());
                                    optionEntity.setRateTypeKo(optionApiDto.getIntrRateTypeNm());
                                    optionEntity.setRsrvType(optionApiDto.getRsrvType());
                                    optionEntity.setRsrvTypeName(optionApiDto.getRsrvTypeNm());
                                    optionEntity.setSaveTime(Integer.parseInt(optionApiDto.getSaveTrm())); // saveTrm을 Integer로 변환
                                    optionEntity.setBasicRate(optionApiDto.getBasicRate());
                                    optionEntity.setSpclRate(optionApiDto.getSpclRate());

                                    // Products 엔티티에 저장
                                    optionEntity.setProducts(product);

                                    // 적금 옵션 엔티티를 저장
                                    installMentOptionsRepository.save(optionEntity);
                                }
                            } catch (Exception e) {
                                throw new ProductServiceExceptionHandler(ErrorStatus.INSERT_INSOPTIONS_ERROR);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new ProductServiceExceptionHandler(ErrorStatus.INSERT_PRD_ERROR);
        }

    }
}
