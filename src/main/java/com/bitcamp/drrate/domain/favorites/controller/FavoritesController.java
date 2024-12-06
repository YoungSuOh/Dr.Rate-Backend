/* src/main/java/com/bitcamp/drrate/domain/favorites/controller/FavoritesController.java */

package com.bitcamp.drrate.domain.favorites.controller;


import com.bitcamp.drrate.domain.favorites.dto.request.FavoritesRequestDTO;
import com.bitcamp.drrate.domain.favorites.dto.response.FavoritesResponseDTO;
import com.bitcamp.drrate.domain.favorites.service.FavoritesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value="/api/favorite")
@RequiredArgsConstructor
public class FavoritesController {

  private final FavoritesService favoritesService;

  /* ProductDetailPage; 즐겨찾기 등록 */
  @PostMapping("/addFavorite")
  public ResponseEntity<FavoritesResponseDTO.FavoriteActionResponseDTO> addFavorite(
      @RequestBody @Valid FavoritesRequestDTO.AddFavoriteDTO request) {

    // 서비스 호출 및 등록 처리
    Long favoriteId = favoritesService.addFavorite(request.getFaUserId(), request.getFaPrdId());

    return ResponseEntity.ok(
        FavoritesResponseDTO.FavoriteActionResponseDTO.builder()
            .favoriteId(favoriteId) // 예시 ID
            .message("즐겨찾기가 등록되었습니다. 즐겨찾기 페이지로 이동하시겠습니까?")
            .build()
    );
  }




  /* ProductDetailPage; 즐겨찾기 취소 */
  // public removeFavorite {}



  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 조회 */
  // public viewFavorite {}


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 삭제 */
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
