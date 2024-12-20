package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.service.ProductsService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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



    
    
    
    //241211 상품전체조회 - 오혜진
    @GetMapping(value = "getAllProducts")
    @ResponseBody
    public List<Map<String, Object>> getAllProducts() {
        return productsService.getAllProducts();
    }
    
    //241211 카테고리 i,d로 조회 - 오혜진
    @GetMapping(value = "getProductsByCtg/{ctg}")
    @ResponseBody
    public List<Products> getProductsByCtg(@PathVariable(value = "ctg") String ctg) {
        return productsService.getProductsByCtg(ctg);
    }
    

}
