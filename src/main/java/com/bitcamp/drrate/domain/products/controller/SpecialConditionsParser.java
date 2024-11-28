package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.dto.request.ProductRequestDTO;
import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialConditionsParser {
    public static List<ProductResponseDTO.ProductCondition> parseSpecialConditions(String specialConditions, BigDecimal basicRate, BigDecimal spclRate) {
        // 조건 리스트
        List<ProductResponseDTO.ProductCondition> conditions = new ArrayList<>();

        // 기본금리와 최대금리 차이
        BigDecimal restRate = spclRate.subtract(basicRate);

        // \n , 로 분리
        String[] lines = specialConditions.split("[\\r\\n,]+");
        // 기본금리와 최대금리 차이 와 조회된 추가 금리의 차이
        BigDecimal totalRate = BigDecimal.ZERO;

        /* 리스트 정리 */
        for (String line : lines) {
            line = removeLeadingSymbols(line);

            if (containsUnwantedKeywords(line)) {
                continue;  // 조건에 '최고우대금리'나 '*'가 포함된 경우 리스트에 추가하지 않음
            }

            String[] parts = line.split(":");

            String description = parts[0].trim();
            String conditionType = extractConditionType(description);
            BigDecimal rate = BigDecimal.ZERO;

            if (parts.length == 2) {
                rate = new BigDecimal(parseRate(parts[1].trim()));
                rate = rate.setScale(2, BigDecimal.ROUND_HALF_UP);
            }else if(parts.length == 1){
                if(parts[0].trim().contains("%")){
                    description = extractDescription(line).trim();  // 설명 부분 추출
                    rate = new BigDecimal(parseRate(parts[0].trim()));
                    rate = rate.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            }

            //System.out.print("- " + parts[0] + ", - " + rate + "\n");
            // 금리 합산
            totalRate = totalRate.add(rate);

            // ProductCondition 객체 생성하여 리스트에 추가
            ProductResponseDTO.ProductCondition condition = ProductResponseDTO.ProductCondition.builder()
                    .description(description)
                    .rate(rate.doubleValue())
                    .conditionType(conditionType)
                    .build();

            conditions.add(condition);
        }

        /* Rate 없는 리스트 처리 */

        // 수정된 조건 List
        List<ProductResponseDTO.ProductCondition> addConditions = new ArrayList<>();
        // 이상 요건 충족시 처리
        StringBuilder addDescription = new StringBuilder();
        // 나머지 Rate
        BigDecimal missingRate = BigDecimal.ZERO;

        //System.out.print("restRate : " + restRate);
        // 부족한 금리 계산
        if (totalRate.compareTo(restRate) < 0) {
            missingRate = restRate.subtract(totalRate);

           // System.out.print("부족한 값 : " + missingRate);

            int count = 0;
            for (ProductResponseDTO.ProductCondition condition : conditions) {
                if(condition.getRate() == 0){
                    count++;
                }
            }

            Double divRate = missingRate.doubleValue() / count;

            for (ProductResponseDTO.ProductCondition condition : conditions) {
                if(condition.getRate() == 0){
                    addConditions.add(ProductResponseDTO.ProductCondition.builder()
                            .description(condition.getDescription())
                            .rate(divRate)
                            .conditionType(condition.getConditionType())
                            .build());
                }else{
                    addConditions.add(condition);
                }
            }
        }else{
            // Rate가 0인 condition 제외하고 담기
            for (ProductResponseDTO.ProductCondition condition : conditions) {
                if(condition.getRate() == 0) {
                    addDescription.append("- " + condition.getDescription() + "\n");
                    continue;
                }else{
                    addConditions.add(condition);
                }
            }


        }

        // Rate가 0인 리스트 처리(이상 요건 충족시 처리)
        for (int i = 0; i < addConditions.size(); i++) {
            ProductResponseDTO.ProductCondition condition = addConditions.get(i);

            if (condition.getDescription().contains("이상 요건 충족")) {
                String combinedDescription = condition.getDescription() + "\n" + addDescription.toString();

                addConditions.set(i, ProductResponseDTO.ProductCondition.builder()
                        .description(combinedDescription)
                        .rate(condition.getRate())
                        .conditionType(condition.getConditionType())
                        .build());
            }

            //System.out.println("\n - " + addConditions.get(i).getDescription() + "\n - " + addConditions.get(i).getRate());
        }

        //System.out.print(addConditions.get(0).getDescription());
        return addConditions;
    }

    /* 금리 우대치에서 숫자만 추출 ("연0.55%" -> 0.55) */
    private static double parseRate(String rateStr) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(rateStr);

        if (matcher.find()) {
            // 일치하는 숫자 추출하여 double로 반환
            return Double.parseDouble(matcher.group());
        } else {
            // 일치하는 값이 없으면 예외를 던지거나 기본값을 반환
            throw new IllegalArgumentException("Rate not found in input string");
        }
    }

    /* 설명 부분만 추출 */
    private static String extractDescription(String line) {
        return line.replaceAll("[0-9.]+%+", "").trim();
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
        if (line.contains("최고우대금리") || line.contains("최대우대금리") || line.contains("최대한도") ||  line.contains("충족") || line.contains("※")) {
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
        return line;
    }
}
