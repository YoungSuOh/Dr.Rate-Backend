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
