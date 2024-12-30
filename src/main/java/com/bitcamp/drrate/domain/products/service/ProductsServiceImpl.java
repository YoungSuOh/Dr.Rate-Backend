package com.bitcamp.drrate.domain.products.service;

import com.amazonaws.services.s3.AmazonS3;
import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.*;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.InstallMentOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.ProductsRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.ProductServiceExceptionHandler;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.ParsingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {
    private final ProductsRepository productsRepository;
    private final DepositeOptionsRepository depositeOptionsRepository;
    private final InstallMentOptionsRepository installMentOptionsRepository;
    private final JPAQueryFactory queryFactory;

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
        System.out.print("prdId: " + prdId + "\n");
        Optional<Products> product;
        List<DepositeOptions> dep_options;
        List<InstallMentOptions> ins_options;

        Map<String, Object> map = new HashMap<>();

        // 상품 조회
        try {
            product = productsRepository.findById(prdId);
            if (!product.isPresent()) {
                throw new ProductServiceExceptionHandler(ErrorStatus.PRODUCT_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ProductServiceExceptionHandler(ErrorStatus.PRD_UNKNOWN_ERROR);
        }

        map.put("product", product.get());

        // 옵션 조회
        try {
            dep_options = depositeOptionsRepository.findByProductsId(prdId); // 예금
            ins_options = installMentOptionsRepository.findByProductsId(prdId); // 적금

            if ((dep_options == null || dep_options.isEmpty()) &&
                    (ins_options == null || ins_options.isEmpty())) {
                throw new ProductServiceExceptionHandler(ErrorStatus.OPTION_NOT_FOUND);
            }

        } catch (Exception e) {
            throw new ProductServiceExceptionHandler(ErrorStatus.PRD_UNKNOWN_ERROR);
        }

        if (dep_options != null && !dep_options.isEmpty()) {
            System.out.println("dep_options: " + dep_options.isEmpty());
            map.put("options", dep_options);
        } else if (ins_options != null && !ins_options.isEmpty()) {
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

                    } catch (ClassCastException e) {
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
        try {
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

    //241211 카테고리 - 오혜진
    @Override
    public List<Products> getProductsByCtg(String ctg) {
        return productsRepository.findByCtg(ctg);
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



    @Override
    public Page<ProductResponseDTO.ProductListDTO> getGuestProduct(Integer page, Integer size, String category,  List<String> bankList, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        QProducts qProducts = QProducts.products;
        // 쿼리 생성
        BooleanBuilder builder = new BooleanBuilder();

        switch (category) {
            // 예금 case
            case "deposit":
                //  카테고리 : 예금(d) or 적금(i)
                builder.and(qProducts.ctg.eq("d"));

                // 예금과 join 예정
                QDepositeOptions dOptions = QDepositeOptions.depositeOptions;

                // 은행 설정
                if (bankList != null) {
                    BooleanBuilder bankCondition = new BooleanBuilder();
                    for (String bank : bankList) {
                        bankCondition.or(qProducts.bankName.eq(bank)); // OR 조건 추가
                    }
                    builder.and(bankCondition);
                }

                // 1차 데이터 조회
                List<ProductResponseDTO.ProductListDTO> qResults = queryFactory
                        .select(
                                Projections.constructor(
                                        ProductResponseDTO.ProductListDTO.class,
                                        qProducts.id,
                                        qProducts.bankLogo,
                                        qProducts.bankName,
                                        qProducts.prdName,
                                        dOptions.basicRate,
                                        dOptions.spclRate
                                )
                        )
                        .from(qProducts)
                        .join(qProducts.depOptions, dOptions)
                        .where(builder)
                        .fetch();

                // 2차 결과 : 중복된 이름의 상품이면 순회하면서 spcl, basic max 값 저장 (중복 제거됨)
                List<ProductResponseDTO.ProductListDTO> dFinalResults = qResults.stream()
                        .collect(Collectors.toMap(
                                ProductResponseDTO.ProductListDTO::getPrdName, // prdName을 키로 사용
                                dto -> dto, // 현재 DTO를 값으로 사용
                                (existing, replacement) -> {
                                    BigDecimal existingBasicRate = new BigDecimal(String.valueOf(existing.getBasicRate() != null ? existing.getBasicRate() : "0"));
                                    BigDecimal replacementBasicRate = new BigDecimal(String.valueOf(replacement.getBasicRate() != null ? replacement.getBasicRate() : "0"));
                                    BigDecimal existingSpclRate = new BigDecimal(String.valueOf(existing.getSpclRate() != null ? existing.getSpclRate() : "0"));
                                    BigDecimal replacementSpclRate = new BigDecimal(String.valueOf(replacement.getSpclRate() != null ? replacement.getSpclRate() : "0"));


                                    // spclRate 비교 및 최댓값 갱신
                                    if (replacementSpclRate.compareTo(existingSpclRate) > 0) {
                                        existing.setSpclRate(replacement.getSpclRate());
                                    }

                                    // basicRate 비교 및 최댓값 갱신
                                    if (replacementBasicRate.compareTo(existingBasicRate) > 0) {
                                        existing.setBasicRate(replacement.getBasicRate());
                                    }

                                    return existing; // 최종 값 반환
                                }
                        ))
                        .values()
                        .stream()
                        .sorted((dto1, dto2) -> {
                            // 정렬 조건에 따른 우선순위 설정
                            BigDecimal dto1SpclRate = new BigDecimal(String.valueOf(dto1.getSpclRate()));
                            BigDecimal dto2SpclRate = new BigDecimal(String.valueOf(dto2.getSpclRate()));
                            BigDecimal dto1BasicRate = new BigDecimal(String.valueOf(dto1.getBasicRate()));
                            BigDecimal dto2BasicRate = new BigDecimal(String.valueOf(dto2.getBasicRate()));

                            if ("spclRate".equals(sort)) {
                                return dto2SpclRate.compareTo(dto1SpclRate); // spclRate 기준 내림차순
                            } else if ("basicRate".equals(sort)) {
                                return dto2BasicRate.compareTo(dto1BasicRate); // basicRate 기준 내림차순
                            } else {
                                return 0; // 정렬 조건이 없으면 그대로 유지
                            }
                        })
                        .collect(Collectors.toList());

                int dTotalElements = dFinalResults.size();

                List<ProductResponseDTO.ProductListDTO> dPaginatedResults = dFinalResults.stream()
                        .skip(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .collect(Collectors.toList());

                // 페이징된 결과 반환
                return new PageImpl<>(dPaginatedResults, pageable, dTotalElements);
            //  적금 case
            case "installment":
                //  카테고리 : 예금(d) or 적금(i)
                builder.and(qProducts.ctg.eq("i"));

                // 적금과 join 예정
                QInstallMentOptions iOptions = QInstallMentOptions.installMentOptions;

                // 은행 설정
                if (bankList != null) {
                    BooleanBuilder bankCondition = new BooleanBuilder();
                    for (String bank : bankList) {
                        bankCondition.or(qProducts.bankName.eq(bank)); // OR 조건 추가
                    }
                    builder.and(bankCondition);
                }


                // 1차 데이터 조회
                List<ProductResponseDTO.ProductListDTO> iResults = queryFactory
                        .select(
                                Projections.constructor(
                                        ProductResponseDTO.ProductListDTO.class,
                                        qProducts.id,
                                        qProducts.bankLogo,
                                        qProducts.bankName,
                                        qProducts.prdName,
                                        iOptions.basicRate,
                                        iOptions.spclRate
                                )
                        )
                        .from(qProducts)
                        .join(qProducts.insOptions, iOptions)
                        .where(builder)
                        .fetch();

                // 2차 결과 : 중복된 이름의 상품이면 순회하면서 spcl, basic max 값 저장 (중복 제거됨)
                List<ProductResponseDTO.ProductListDTO> iFinalResults = iResults.stream()
                        .collect(Collectors.toMap(
                                ProductResponseDTO.ProductListDTO::getPrdName, // prdName을 키로 사용
                                dto -> dto, // 현재 DTO를 값으로 사용
                                (existing, replacement) -> {
                                    BigDecimal existingBasicRate = new BigDecimal(String.valueOf(existing.getBasicRate() != null ? existing.getBasicRate() : "0"));
                                    BigDecimal replacementBasicRate = new BigDecimal(String.valueOf(replacement.getBasicRate() != null ? replacement.getBasicRate() : "0"));
                                    BigDecimal existingSpclRate = new BigDecimal(String.valueOf(existing.getSpclRate() != null ? existing.getSpclRate() : "0"));
                                    BigDecimal replacementSpclRate = new BigDecimal(String.valueOf(replacement.getSpclRate() != null ? replacement.getSpclRate() : "0"));


                                    // spclRate 비교 및 최댓값 갱신
                                    if (replacementSpclRate.compareTo(existingSpclRate) > 0) {
                                        existing.setSpclRate(replacement.getSpclRate());
                                    }

                                    // basicRate 비교 및 최댓값 갱신
                                    if (replacementBasicRate.compareTo(existingBasicRate) > 0) {
                                        existing.setBasicRate(replacement.getBasicRate());
                                    }

                                    return existing; // 최종 값 반환
                                }
                        ))
                        .values()
                        .stream()
                        .sorted((dto1, dto2) -> {
                            // 정렬 조건에 따른 우선순위 설정
                            BigDecimal dto1SpclRate = new BigDecimal(String.valueOf(dto1.getSpclRate()));
                            BigDecimal dto2SpclRate = new BigDecimal(String.valueOf(dto2.getSpclRate()));
                            BigDecimal dto1BasicRate = new BigDecimal(String.valueOf(dto1.getBasicRate()));
                            BigDecimal dto2BasicRate = new BigDecimal(String.valueOf(dto2.getBasicRate()));

                            if ("spclRate".equals(sort)) {
                                return dto2SpclRate.compareTo(dto1SpclRate); // spclRate 기준 내림차순
                            } else if ("basicRate".equals(sort)) {
                                return dto2BasicRate.compareTo(dto1BasicRate); // basicRate 기준 내림차순
                            } else {
                                return 0; // 정렬 조건이 없으면 그대로 유지
                            }
                        })
                        .collect(Collectors.toList());

                int iTotalElements = iFinalResults.size();

                List<ProductResponseDTO.ProductListDTO> iPaginatedResults = iFinalResults.stream()
                        .skip(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .collect(Collectors.toList());

                // 페이징된 결과 반환
                return new PageImpl<>(iPaginatedResults, pageable, iTotalElements);
            // 예금 적금 아니면 예외 처리
            default:
                throw new ProductServiceExceptionHandler(ErrorStatus.PRODUCT_BAD_REQUEST);
        }
    }


    @Transactional
    @Override
    public Page<ProductResponseDTO.ProductListDTO> getProduct(Integer page, Integer size, String category, List<String> bankList, Integer age, Integer period, String rate, String join, String sort) {

        Pageable pageable = PageRequest.of(page, size);
        QProducts qProducts = QProducts.products;
        // 쿼리 생성
        BooleanBuilder builder = new BooleanBuilder();

        switch (category) {
            // 예금 case
            case "deposit":
                //  카테고리 : 예금(d) or 적금(i)
                builder.and(qProducts.ctg.eq("d"));

                // 예금과 join 예정
                QDepositeOptions dOptions = QDepositeOptions.depositeOptions;

                // 은행 설정
                if (bankList != null) {
                    BooleanBuilder bankCondition = new BooleanBuilder();
                    for (String bank : bankList) {
                        bankCondition.or(qProducts.bankName.eq(bank)); // OR 조건 추가
                    }
                    builder.and(bankCondition);
                }

                // 나이 설정
                if (age != null) {
                    if (age < 0) {
                        throw new ProductServiceExceptionHandler(ErrorStatus.PRODUCT_BAD_REQUEST);
                    } else {
                        builder.and(qProducts.joinMemberAge.goe(age));
                    }
                }

                // 기한 설정
                if (period != null) {
                    if (period < 0) {
                        throw new ProductServiceExceptionHandler(ErrorStatus.PRODUCT_BAD_REQUEST);
                    } else {
                        builder.and(dOptions.saveTime.goe(period));
                    }
                }

                // 복리, 단리
                if (rate != null) {
                    builder.and(dOptions.rateTypeKo.eq(rate));
                }

                // 대면 & 비대면 설정
                if (join != null) {
                    if (join.equals("대면")) {
                        builder.and(qProducts.joinWay.contains("영업점"));
                    } else if (join.equals("비대면")) {
                        builder.and(qProducts.joinWay.contains("인터넷")
                                .or(qProducts.joinWay.contains("스마트폰"))
                                .or(qProducts.joinWay.contains("전화")));
                    }
                }



                // 1차 데이터 조회
                List<ProductResponseDTO.ProductListDTO> qResults = queryFactory
                        .select(
                                Projections.constructor(
                                        ProductResponseDTO.ProductListDTO.class,
                                        qProducts.id,
                                        qProducts.bankLogo,
                                        qProducts.bankName,
                                        qProducts.prdName,
                                        dOptions.basicRate,
                                        dOptions.spclRate
                                )
                        )
                        .from(qProducts)
                        .join(qProducts.depOptions, dOptions)
                        .where(builder)
                        .fetch();



                // 2차 결과 : 중복된 이름의 상품이면 순회하면서 spcl, basic max 값 저장 (중복 제거됨)
                List<ProductResponseDTO.ProductListDTO> dFinalResults = qResults.stream()
                        .collect(Collectors.toMap(
                                ProductResponseDTO.ProductListDTO::getPrdName, // prdName을 키로 사용
                                dto -> dto, // 현재 DTO를 값으로 사용
                                (existing, replacement) -> {
                                    BigDecimal existingBasicRate = new BigDecimal(String.valueOf(existing.getBasicRate() != null ? existing.getBasicRate() : "0"));
                                    BigDecimal replacementBasicRate = new BigDecimal(String.valueOf(replacement.getBasicRate() != null ? replacement.getBasicRate() : "0"));
                                    BigDecimal existingSpclRate = new BigDecimal(String.valueOf(existing.getSpclRate() != null ? existing.getSpclRate() : "0"));
                                    BigDecimal replacementSpclRate = new BigDecimal(String.valueOf(replacement.getSpclRate() != null ? replacement.getSpclRate() : "0"));


                                    // spclRate 비교 및 최댓값 갱신
                                    if (replacementSpclRate.compareTo(existingSpclRate) > 0) {
                                        existing.setSpclRate(replacement.getSpclRate());
                                    }

                                    // basicRate 비교 및 최댓값 갱신
                                    if (replacementBasicRate.compareTo(existingBasicRate) > 0) {
                                        existing.setBasicRate(replacement.getBasicRate());
                                    }

                                    return existing; // 최종 값 반환
                                }
                        ))
                        .values()
                        .stream()
                        .sorted((dto1, dto2) -> {
                            // 정렬 조건에 따른 우선순위 설정
                            BigDecimal dto1SpclRate = new BigDecimal(String.valueOf(dto1.getSpclRate()));
                            BigDecimal dto2SpclRate = new BigDecimal(String.valueOf(dto2.getSpclRate()));
                            BigDecimal dto1BasicRate = new BigDecimal(String.valueOf(dto1.getBasicRate()));
                            BigDecimal dto2BasicRate = new BigDecimal(String.valueOf(dto2.getBasicRate()));

                            if ("spclRate".equals(sort)) {
                                return dto2SpclRate.compareTo(dto1SpclRate); // spclRate 기준 내림차순
                            } else if ("basicRate".equals(sort)) {
                                return dto2BasicRate.compareTo(dto1BasicRate); // basicRate 기준 내림차순
                            } else {
                                return 0; // 정렬 조건이 없으면 그대로 유지
                            }
                        })
                        .collect(Collectors.toList());

                int dTotalElements = dFinalResults.size();

                List<ProductResponseDTO.ProductListDTO> dPaginatedResults = dFinalResults.stream()
                        .skip(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .collect(Collectors.toList());

                 // 페이징된 결과 반환
                return new PageImpl<>(dPaginatedResults, pageable, dTotalElements);
            //  적금 case
            case "installment":
                //  카테고리 : 예금(d) or 적금(i)
                builder.and(qProducts.ctg.eq("i"));

                // 적금과 join 예정
                QInstallMentOptions iOptions = QInstallMentOptions.installMentOptions;

                // 은행 설정
                if (bankList != null) {
                    BooleanBuilder bankCondition = new BooleanBuilder();
                    for (String bank : bankList) {
                        bankCondition.or(qProducts.bankName.eq(bank)); // OR 조건 추가
                    }
                    builder.and(bankCondition);
                }

                // 나이 설정
                if (age != null) {
                    if (age < 0) {
                        throw new ProductServiceExceptionHandler(ErrorStatus.PRODUCT_BAD_REQUEST);
                    } else {
                        builder.and(qProducts.joinMemberAge.goe(age));
                    }
                }

                // 기한 설정
                if (period != null) {
                    if (period < 0) {
                        throw new ProductServiceExceptionHandler(ErrorStatus.PRODUCT_BAD_REQUEST);
                    } else {
                        builder.and(iOptions.saveTime.goe(period));
                    }
                }

                // 복리, 단리
                if (rate != null) {
                    builder.and(iOptions.rateTypeKo.eq(rate));
                }

                // 대면 & 비대면 설정
                if (join != null) {
                    if (join.equals("대면")) {
                        builder.and(qProducts.joinWay.contains("영업점"));
                    } else if (join.equals("비대면")) {
                        builder.and(qProducts.joinWay.contains("인터넷")
                                .or(qProducts.joinWay.contains("스마트폰"))
                                .or(qProducts.joinWay.contains("전화")));
                    }
                }



                // 1차 데이터 조회
                List<ProductResponseDTO.ProductListDTO> iResults = queryFactory
                        .select(
                                Projections.constructor(
                                        ProductResponseDTO.ProductListDTO.class,
                                        qProducts.id,
                                        qProducts.bankLogo,
                                        qProducts.bankName,
                                        qProducts.prdName,
                                        iOptions.basicRate,
                                        iOptions.spclRate
                                )
                        )
                        .from(qProducts)
                        .join(qProducts.insOptions, iOptions)
                        .where(builder)
                        .fetch();

                // 2차 결과 : 중복된 이름의 상품이면 순회하면서 spcl, basic max 값 저장 (중복 제거됨)
                List<ProductResponseDTO.ProductListDTO> iFinalResults = iResults.stream()
                        .collect(Collectors.toMap(
                                ProductResponseDTO.ProductListDTO::getPrdName, // prdName을 키로 사용
                                dto -> dto, // 현재 DTO를 값으로 사용
                                (existing, replacement) -> {
                                    BigDecimal existingBasicRate = new BigDecimal(String.valueOf(existing.getBasicRate() != null ? existing.getBasicRate() : "0"));
                                    BigDecimal replacementBasicRate = new BigDecimal(String.valueOf(replacement.getBasicRate() != null ? replacement.getBasicRate() : "0"));
                                    BigDecimal existingSpclRate = new BigDecimal(String.valueOf(existing.getSpclRate() != null ? existing.getSpclRate() : "0"));
                                    BigDecimal replacementSpclRate = new BigDecimal(String.valueOf(replacement.getSpclRate() != null ? replacement.getSpclRate() : "0"));

                                    // spclRate 비교 및 최댓값 갱신
                                    if (replacementSpclRate.compareTo(existingSpclRate) > 0) {
                                        existing.setSpclRate(replacement.getSpclRate());
                                    }

                                    // basicRate 비교 및 최댓값 갱신
                                    if (replacementBasicRate.compareTo(existingBasicRate) > 0) {
                                        existing.setBasicRate(replacement.getBasicRate());
                                    }

                                    return existing; // 최종 값 반환
                                }
                        ))
                        .values()
                        .stream()
                        .sorted((dto1, dto2) -> {
                            // 정렬 조건에 따른 우선순위 설정
                            BigDecimal dto1SpclRate = new BigDecimal(String.valueOf(dto1.getSpclRate()));
                            BigDecimal dto2SpclRate = new BigDecimal(String.valueOf(dto2.getSpclRate()));
                            BigDecimal dto1BasicRate = new BigDecimal(String.valueOf(dto1.getBasicRate()));
                            BigDecimal dto2BasicRate = new BigDecimal(String.valueOf(dto2.getBasicRate()));

                            if ("spclRate".equals(sort)) {
                                return dto2SpclRate.compareTo(dto1SpclRate); // spclRate 기준 내림차순
                            } else if ("basicRate".equals(sort)) {
                                return dto2BasicRate.compareTo(dto1BasicRate); // basicRate 기준 내림차순
                            } else {
                                return 0; // 정렬 조건이 없으면 그대로 유지
                            }
                        })
                        .collect(Collectors.toList());

                int iTotalElements = iFinalResults.size();

                List<ProductResponseDTO.ProductListDTO> iPaginatedResults = iFinalResults.stream()
                        .skip(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .collect(Collectors.toList());

                // 페이징된 결과 반환
                return new PageImpl<>(iPaginatedResults, pageable, iTotalElements);
            // 예금 적금 아니면 예외 처리
            default:
                throw new ProductServiceExceptionHandler(ErrorStatus.PRODUCT_BAD_REQUEST);
        }
    }

}
