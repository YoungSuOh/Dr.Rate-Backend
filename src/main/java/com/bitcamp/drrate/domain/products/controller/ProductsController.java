package com.bitcamp.drrate.domain.products.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.service.ProductsService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(value="product")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;

    /* 상품 상세페이지 상품 출력 */
    @GetMapping(value="getOneProduct/{prdId}")
    public Map<String, Object> getOneProduct(@PathVariable(value="prdId") String prd_id) {

        //System.out.println(prd_id);
        Map<String, Object> map = productsService.getOneProduct(prd_id);

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
