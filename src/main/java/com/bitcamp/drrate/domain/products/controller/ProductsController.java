package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.dto.response.DepResponseDTO;
import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.service.ProductsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@CrossOrigin
@RequestMapping(value="product")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;

    @GetMapping(value="getOneProduct/{id}")
    @ResponseBody
    public Map<String, Object> getOneProduct(@PathVariable(value="id") String prd_id) {

        System.out.println(prd_id);
        Map<String, Object> map = productsService.getOneProduct(prd_id);

        Optional<Products> optionalProduct = (Optional<Products>) map.get("product");
        String specialConditions;

        BigDecimal basicRate = BigDecimal.ZERO;  // BigDecimal.ZERO는 0 값을 나타냄
        BigDecimal spclRate = BigDecimal.ZERO;
        int optionNum = 0;

        if (optionalProduct.isPresent()) {
            Products product = optionalProduct.get();
            specialConditions = product.getSpclCnd();

            // 옵션
            List<?> options = (List<?>) map.get("options");

            if (options != null && !options.isEmpty()) {
                // options가 DepositeOptions일 경우
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i) instanceof DepositeOptions) {
                        DepositeOptions option = (DepositeOptions) options.get(i);

                        // '더 큰' spclRate를 찾는 조건으로 수정
                        if (spclRate.compareTo(option.getSpclRate()) < 0) {  // 현재 spclRate보다 더 큰 값을 찾기
                            spclRate = option.getSpclRate();
                            basicRate = option.getBasicRate();
                            optionNum = i;
                        }
                    }
                    // options가 InstallMentOptions일 경우
                    else if (options.get(i) instanceof InstallMentOptions) {
                        InstallMentOptions option = (InstallMentOptions) options.get(i);

                        // '더 큰' spclRate를 찾는 조건으로 수정
                        if (spclRate.compareTo(option.getSpclRate()) < 0) {  // 현재 spclRate보다 더 큰 값을 찾기
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

}
