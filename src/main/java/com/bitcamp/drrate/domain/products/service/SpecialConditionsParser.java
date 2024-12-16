package com.bitcamp.drrate.domain.products.service;

import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.ProductServiceExceptionHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialConditionsParser {
    public static List<ProductResponseDTO.ProductCondition> parseSpecialConditions(String specialConditions, BigDecimal basicRate, BigDecimal spclRate) {
        // 조건 리스트
        List<ProductResponseDTO.ProductCondition> conditions = new ArrayList<>();

        try {
        // 기본금리와 최대금리 차이
        BigDecimal restRate = spclRate.subtract(basicRate);
        // 기본금리와 최대금리 차이 와 조회된 추가 금리의 차이
        BigDecimal totalRate = BigDecimal.ZERO;


        /* 조건 리스트로 변경 */
        // 단, 일 경우 제외
        specialConditions = specialConditions.replaceAll("단, +", "<<DO_NOT_SPLIT>>");
        // \n , 로 분리
        String[] lines = specialConditions.split("[\\r\\n,]+");
        // 단, 으로 다시 변경
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replace("<<DO_NOT_SPLIT>>", "단, ");
        }

        /* 리스트 정리 */
        for (String line : lines) {
            // 불필요한 기호 삭제
            line = removeLeadingSymbols(line);

            // 조건에 '최고우대금리'나 '*'가 포함된 경우 리스트에 추가하지 않음
            if (containsUnwantedKeywords(line)) {
                continue;
            }

            // : 로 구분
            String[] parts = line.split(":");

            String description = "";
            String conditionType = extractConditionType(description);
            BigDecimal rate = BigDecimal.ZERO;

            // 조건과 금리 나누기
            if (parts.length == 2) {
                // parts[0]이 금리인 경우
                if (parts[0].trim().contains("%")) {
                    rate = new BigDecimal(parseRate(parts[0].trim()));
                    description = parts[1].trim();

                // parts[1]이 금리인 경우
                } else if (parts[1].trim().contains("%")) {
                    rate = new BigDecimal(parseRate(parts[1].trim()));
                    description = parts[0].trim();

                // 둘 다 금리가 아니고 설명일 경우
                } else {
                    description = parts[0].trim() + " " + parts[1].trim();
                    rate = BigDecimal.ZERO;
                }

            // 금리만 있을 경우
            } else if (parts.length == 1 && parts[0].trim().contains("%")) {
                description = extractDescription(line).trim();
                rate = new BigDecimal(parseRate(parts[0].trim()));
                rate = rate.setScale(2, BigDecimal.ROUND_HALF_UP);

            // 크기가 3인 경우
            } else if (parts.length == 3 && parts[2].trim().contains("%")) {
                description = parts[0].trim() + " : " + parts[1].trim();
                rate = new BigDecimal(parseRate(parts[2].trim()));

            // 그 외
            } else {
                description = parts[0].trim();
                rate = BigDecimal.ZERO;
            }

            // System.out.print("- " + parts[0] + ", - " + rate + "\n");

            // 금리 합산
            totalRate = totalRate.add(rate);

            // 정리한 리스트 넣기
            try{
            ProductResponseDTO.ProductCondition condition = ProductResponseDTO.ProductCondition.builder()
                    .description(description)
                    .rate(rate.doubleValue())
                    .conditionType(conditionType)
                    .build();

            conditions.add(condition);
            } catch (Exception e) {
                throw new ProductServiceExceptionHandler(ErrorStatus.CONDITIONS_SPECIAL_INSERT_ERROR);
            }
        }


        /* Rate 없는 리스트 처리 */

        // 수정된 조건 List
        List<ProductResponseDTO.ProductCondition> addConditions = new ArrayList<>();

        // 나머지 Rate
        BigDecimal missingRate = BigDecimal.ZERO;

        // 부족한 금리 계산
        if (totalRate.compareTo(restRate) < 0) {
            missingRate = restRate.subtract(totalRate);

            int count = 0;
            for (ProductResponseDTO.ProductCondition condition : conditions) {
                if(condition.getRate() == 0){
                    count++;
                }
            }

            Double divRate = missingRate.doubleValue() / count;

            for (ProductResponseDTO.ProductCondition condition : conditions) {
                if(BigDecimal.ZERO.compareTo(new BigDecimal(condition.getRate())) == 0){
                    addConditions.add(ProductResponseDTO.ProductCondition.builder()
                            .description(condition.getDescription())
                            .rate(divRate)
                            .conditionType(condition.getConditionType())
                            .build());
                }else{
                    addConditions.add(condition);
                }
            }

        // 최대 우대 금리가 부족하지 않을 때
        }else{
            // Rate가 0인 condition 제외하고 담기
            for (int i = 0; i < conditions.size(); i++) {
                ProductResponseDTO.ProductCondition condition = conditions.get(i);

                if (condition.getRate() == 0) {
                    // Rate가 0인 조건은 바로 앞의 description에 추가
                    if (addConditions.size() > 0) {
                        // 앞선 조건이 있다면 그 조건에 description 추가
                        ProductResponseDTO.ProductCondition previousCondition = addConditions.get(addConditions.size() - 1);
                        String combinedDescription = previousCondition.getDescription() + "\n- " + condition.getDescription();

                        addConditions.set(addConditions.size() - 1, ProductResponseDTO.ProductCondition.builder()
                                .description(combinedDescription)
                                .rate(previousCondition.getRate())
                                .conditionType(previousCondition.getConditionType())
                                .build());
                    }
                } else {
                    // 금리가 0이 아닌 조건은 그대로 addConditions에 추가
                    String description = condition.getDescription();

                    addConditions.add(ProductResponseDTO.ProductCondition.builder()
                            .description(description)
                            .rate(condition.getRate())
                            .conditionType(condition.getConditionType())
                            .build());
                }
            }
        }
        
        return addConditions;

        } catch (Exception e) {
            throw new ProductServiceExceptionHandler(ErrorStatus.CONDITIONS_SPECIAL_PARSE_ERROR);
        }
    }

    /* 금리 우대치에서 숫자만 추출 ("연0.55%" -> 0.55) */
    private static double parseRate(String rateStr) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(rateStr);

        if (matcher.find()) {
            // 일치하는 숫자 추출하여 double로 반환
            return Double.parseDouble(matcher.group());
        } else {
            throw new ProductServiceExceptionHandler(ErrorStatus.CONDITIONS_SPECIAL_PARSERATE_ERROR);
        }
    }

    /* 설명 부분만 추출 */
    private static String extractDescription(String line) {
        return line.replaceAll("(연|%|p|제공|[0-9]+(?:\\.[0-9]+)?)", "").trim();
    }

    /* 대면 비대면 */
    private static String extractConditionType(String description) {
        if (description.contains("인터넷")) {
            return "인터넷/모바일뱅킹";
        }
        return "대면";
    }

    /* 제외사항 처리 */
    private static boolean containsUnwantedKeywords(String line) {
        if (line.contains("최고우대금리") || line.contains("최대우대금리") || line.contains("최대한도") ||  line.contains("충족") ||  line.contains("최고 연") || line.contains("단위") || line.contains("각 연")) {
            return true;
        }
        return false;
    }

    /* 특수기호 제거 */
    private static String removeLeadingSymbols(String line) {
        // 한글, 숫자, 기호(①, ②, ③ 등) 뒤 점(.)과 공백을 제거
        line = line.replaceAll("^[가-힣①-⑩0-9]+\\.\\s?", "").trim();  // 한글, 숫자 뒤 점(.)과 공백을 제거
        // 공백있는 부분
        line = line.replaceAll("^[가-힣①-⑩0-9]+\\. \\s?", "").trim();  // 한글, 숫자 뒤 점(.)과 공백을 제거
        // 기호나 문자 외에 불필요한 부분 제거
        line = line.replaceAll("^[^a-zA-Z0-9가-힣]", "").trim();  // 영문, 숫자, 한글을 제외한 문자를 제거
        // 특수 기호 제거
        line = line.replaceAll("[*※]", "").trim();
        return line;
    }
}
