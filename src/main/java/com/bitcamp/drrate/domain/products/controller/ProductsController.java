package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.service.ProductsService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value="product")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;

    /* 상품 상세페이지 상품 출력 */
    @GetMapping(value="getOneProduct/{prdId}")
    public Map<String, Object> getOneProduct(
            @PathVariable(value="prdId") String prd_id  // URL 경로 파라미터 - 상품 번호
    ) {
        // Long prdId = productService.getPrdId(prd_id); // prdId 확인 처리

        //System.out.println(prd_id);
        Map<String, Object> map = productsService.getOneProduct(prd_id);

        return map;
    }

    /* 즐겨찾기 넣기 */
    @PostMapping(value="favoriteInsert/{prdId}")
    public ResponseEntity<Void> favoriteInsert(
                @AuthenticationPrincipal CustomUserDetails userDetails, // JWT, 인증된 사용자 정보
                @PathVariable(value="prdId") String prd_id
    ) {
        // 데이터 처리 로직 추가
        // Long prdUserId = usersServer.getUserId(userDetails.getId()); // UserId 확인 처리
        // Long prdId = productService.getPrdId(prd_id);

        return ResponseEntity.ok().build(); // HTTP 200 OK 응답
    }
}
