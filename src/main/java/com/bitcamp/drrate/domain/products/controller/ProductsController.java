package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@CrossOrigin
@RequestMapping(value="product")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;

    /* 상품 상세페이지 상품 출력 */
    @GetMapping(value="getOneProduct/{id}")
    @ResponseBody
    public Map<String, Object> getOneProduct(@PathVariable(value="id") String prd_id) {

        //System.out.println(prd_id);
        Map<String, Object> map = productsService.getOneProduct(prd_id);

        // 상품
        Optional<Products> optionalProduct = (Optional<Products>) map.get("product");
        String specialConditions;

        BigDecimal basicRate = BigDecimal.ZERO;
        BigDecimal spclRate = BigDecimal.ZERO;
        int optionNum = 0;

        if (optionalProduct.isPresent()) {
            Products product = optionalProduct.get();
            specialConditions = product.getSpclCnd();

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

    /* 즐겨찾기 넣기 */
    @PostMapping(value="favoriteInsert/{id}")
    public ResponseEntity<Void> favoriteInsert(@PathVariable(value="id") String prd_id,
                                               @RequestHeader(value="userId") String user_id) {
        // 데이터 처리 로직 추가
        System.out.println("Product ID: " + prd_id);
        System.out.println("User ID: " + user_id);

        return ResponseEntity.ok().build(); // HTTP 200 OK 응답
    }
}
