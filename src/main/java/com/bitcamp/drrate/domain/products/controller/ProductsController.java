package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.service.ProductsService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.bitcamp.drrate.domain.products.entity.Products;

@RestController
@RequestMapping(value="/api/product")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;

    /* 상품 상세페이지 상품 출력 */
    @GetMapping(value="getOneProduct/{prdId}")
    public Map<String, Object> getOneProduct(
            @PathVariable(value="prdId") String prd_id  // URL 경로 파라미터 - 상품 번호
    ) {
        Long prdId = productsService.getPrdId(prd_id); // prdId 확인 처리

        System.out.println(prdId);
        Map<String, Object> map = productsService.getOneProduct(prdId);

        return map;
    }

    @GetMapping(value = "/getDeposite/{page}")
    public ApiResponse<List<Map<String, Object>>> getProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,  
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "bank", required = false) String bank, // 은행
            @RequestParam(value = "age", required = false) int age,  // 나이
            @RequestParam(value = "period", required = false) int period, // 기간
            @RequestParam(value = "rate", required = false) String rate,  // 이자 방식
            @RequestParam(value = "join", required = false) String join,  // 가입 방식
            @RequestParam(value="spclRate", required = false) boolean spclRate,  // 최고 금리순
            @RequestParam(value="basicRate", required = false) boolean basicRate  // 기본 금리순          
    ) {
        try{
            List<Map<String, Object>>result = productsService.getProduct(page, size, bank, age, period, rate, join, spclRate, basicRate);
            return ApiResponse.onSuccess(result, SuccessStatus.DEPOSITE_GET_SUCCESS);
        }catch (Exception e){
            return ApiResponse.onFailure(ErrorStatus.DEPOSITE_GET_FAILED.getCode(), ErrorStatus.DEPOSITE_GET_FAILED.getMessage(),null );
        }
    }




    //241211 카테고리 i,d로 조회 - 오혜진
    @GetMapping(value = "getProductsByCtg/{ctg}")
    @ResponseBody
    public List<Products> getProductsByCtg(@PathVariable(value = "ctg") String ctg) {
        return productsService.getProductsByCtg(ctg);
    }
    

}
