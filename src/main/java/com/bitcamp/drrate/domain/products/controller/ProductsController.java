package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value="product")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;

    /* 상품 상세페이지 상품 출력 */
    @GetMapping(value="getOneProduct/{id}")
    public Map<String, Object> getOneProduct(@PathVariable(value="id") String prd_id) {

        //System.out.println(prd_id);
        Map<String, Object> map = productsService.getOneProduct(prd_id);

        return map;
    }


}
