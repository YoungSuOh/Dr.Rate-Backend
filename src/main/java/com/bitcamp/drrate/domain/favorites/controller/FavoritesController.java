/* src/main/java/com/bitcamp/drrate/domain/favorites/controller/FavoritesController.java */

package com.bitcamp.drrate.domain.favorites.controller;


import com.bitcamp.drrate.domain.favorites.dto.request.FavoritesRequestDTO;
import com.bitcamp.drrate.domain.favorites.dto.response.FavoritesResponseDTO;
import com.bitcamp.drrate.domain.favorites.service.FavoritesService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value="/api/favorite")
@RequiredArgsConstructor
public class FavoritesController {

  private final FavoritesService favoritesService;


  /* ProductDetailPage; 즐겨찾기 조회 */
  @GetMapping("/checkFavorite")
  public ResponseEntity<Boolean> checkFavorite(
      @AuthenticationPrincipal CustomUserDetails userDetails, // JWT; 인증된 사용자 정보 가져오기 >> 나중에 UserDetails 로 바꿔야함
      @RequestParam Long faPrdId // 요청 본문에서 즐겨찾기 등록 데이터를 가져옴
  ) {
    // 1. 사용자 ID(PK)를 JWT에서 추출
    Long faUserId = userDetails.getId();

    // CustomUserDetails를 이용하여 사용자 ID를 추출
    // Long faUserId = ((CustomUserDetails) userDetails).getUsers().getId();


    // 2. 서비스 호출: 추출한 faUserId와 요청으로 전달된 faPrdId를 FavoritesService에 전달
    boolean isFavorite = favoritesService.isFavorite(faUserId, faPrdId);

    // 3. 응답 구성: 서비스 호출로 반환된 favoriteId를 응답 DTO에 담아 클라이언트로 반환
    return ResponseEntity.ok(isFavorite);  // HTTP 200 OK 응답; ResponseEntity는 HTTP 상태 코드와 함께 데이터를 반환하기 위한 객체
  }



  /* ProductDetailPage; 즐겨찾기 등록 */
  @PostMapping("/addFavorite")
  public ResponseEntity<FavoritesResponseDTO.ProductFavoriteActionDTO> addFavorite(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid FavoritesRequestDTO.ProductFavoriteDTO request
  ) {

    Long faUserId = userDetails.getId();
    Long faPrdId = request.getFaPrdId(); // 요청으로 전달된 상품 ID(request.getFaPrdId())를 faPrdId에 저장

    favoritesService.addFavorite(faUserId, faPrdId);

    return ResponseEntity.ok(
        FavoritesResponseDTO.ProductFavoriteActionDTO.builder()
            .message("즐겨찾기가 등록되었습니다. 즐겨찾기 페이지로 이동하시겠습니까?")
            .build()
    );
  }



  /* ProductDetailPage; 즐겨찾기 취소 */
  @DeleteMapping("/removeFavorite")
  public ResponseEntity<FavoritesResponseDTO.ProductFavoriteActionDTO> removeFavorite(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid FavoritesRequestDTO.ProductFavoriteDTO request
  ) {
    Long faUserId = userDetails.getId();
    Long faPrdId = request.getFaPrdId();

    favoritesService.removeFavorite(faUserId, faPrdId);

    return ResponseEntity.ok(
        FavoritesResponseDTO.ProductFavoriteActionDTO.builder()
            .message("즐겨찾기가 삭제되었습니다.")
            .build()
    );
  }







  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 조회 */
  // public viewFavorite {}


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 삭제 */
  // public deleteFavorite {}
  // 1 개 삭제 혹은 여러 개 삭제
  // 여러 개 삭제 가능하도록 체크박스 입력 받음, 그 체크박스 상품 아이디를 배열로 받아야함
  
  


}

//  @PostMapping(value="favoriteInsert/{id}")
//  public ResponseEntity<Void> favoriteInsert(@PathVariable(value="id") String prd_id,
//                                             @RequestHeader(value="userId") String user_id) {
//    // 데이터 처리 로직 추가
//    System.out.println("Product ID: " + prd_id);
//    System.out.println("User ID: " + user_id);
//
//    return ResponseEntity.ok().build(); // HTTP 200 OK 응답
//  }
