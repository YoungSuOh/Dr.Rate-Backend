package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.dto.response.ProductResponseDTO;
import com.bitcamp.drrate.domain.products.service.ProductsService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
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

    @GetMapping(value = "/guest/getProduct")
    public ApiResponse<List<ProductResponseDTO.ProductListDTO>> getGuestProduct(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "category", required = true) String category,
            @RequestParam(value = "banks", required = false) String banks, // 은행
            @RequestParam(value="sort", required = false, defaultValue = "spclRate") String sort
    ) {
        try{
            List<String> bankList = banks != null ? Arrays.asList(banks.split(",")) : Collections.emptyList();

            List<ProductResponseDTO.ProductListDTO>result = productsService.getGuestProduct(page, size, category, bankList, sort);
            return ApiResponse.onSuccess(result, SuccessStatus.PRODUCT_GET_SUCCESS);
        }catch (Exception e){
            return ApiResponse.onFailure(ErrorStatus.PRODUCT_NOT_FOUND.getCode(), ErrorStatus.PRODUCT_NOT_FOUND.getMessage(),null );
        }
    }

    @GetMapping(value = "/getProduct")
    public ApiResponse<List<ProductResponseDTO.ProductListDTO>> getProducts(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "category", required = true) String category,
            @RequestParam(value = "banks", required = false) String banks, // 은행
            @RequestParam(value = "age", required = false) Integer age,  // 나이
            @RequestParam(value = "period", required = false) Integer period, // 기간
            @RequestParam(value = "rate", required = false) String rate,  // 이자 방식
            @RequestParam(value = "join", required = false) String join,  // 가입 방식
            @RequestParam(value="sort", required = false, defaultValue = "spclRate") String sort
    ) {
        try{
            List<String> bankList = banks != null ? Arrays.asList(banks.split(",")) : Collections.emptyList();

            List<ProductResponseDTO.ProductListDTO>result = productsService.getProduct(page, size, category, bankList, age, period, rate, join, sort);
            return ApiResponse.onSuccess(result, SuccessStatus.PRODUCT_GET_SUCCESS);
        }catch (Exception e){
            return ApiResponse.onFailure(ErrorStatus.PRODUCT_NOT_FOUND.getCode(), ErrorStatus.PRODUCT_NOT_FOUND.getMessage(),null );
        }
    }


/*

    //241211 카테고리 i,d로 조회 - 오혜진
    @GetMapping(value = "getProductsByCtg/{ctg}")
    @ResponseBody
    public List<Products> getProductsByCtg(@PathVariable(value = "ctg") String ctg) {
        return productsService.getProductsByCtg(ctg);
    }*/
    

}
