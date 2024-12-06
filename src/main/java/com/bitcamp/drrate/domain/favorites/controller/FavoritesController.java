/* src/main/java/com/bitcamp/drrate/domain/favorites/controller/FavoritesController.java */

package com.bitcamp.drrate.domain.favorites.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value="/api/favorite")
@RequiredArgsConstructor
public class FavoritesController {


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 조회 */
  // public viewFavorite {}

  // public deleteFavorite {}
  // 여러 개 삭제 가능하도록 체크박스 입력 받음, 그 체크박스 상품 아이디를 배열로 받아야함
  
  
  /* ProductDetailPage; 즐겨찾기 등록 */
  // public addFavorite {}

//  @PostMapping(value="favoriteInsert/{id}")
//  public ResponseEntity<Void> favoriteInsert(@PathVariable(value="id") String prd_id,
//                                             @RequestHeader(value="userId") String user_id) {
//    // 데이터 처리 로직 추가
//    System.out.println("Product ID: " + prd_id);
//    System.out.println("User ID: " + user_id);
//
//    return ResponseEntity.ok().build(); // HTTP 200 OK 응답
//  }


  /* ProductDetailPage; 즐겨찾기 취소 */
  // public removeFavorite {}



}
