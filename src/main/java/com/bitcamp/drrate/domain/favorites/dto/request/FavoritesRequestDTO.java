/* src/main/java/com/bitcamp/drrate/domain/favorites/dto/request/FavoritesRequestDTO.java */

package com.bitcamp.drrate.domain.favorites.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;


public class FavoritesRequestDTO {

  /* ProductDetailPage; 즐겨찾기 조회, 등록, 취소 요청 */
  @Builder
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ProductFavoriteDTO {
    @NotNull
    private Long prdId; // 상품 ID
  }


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 조회 요청 */
  @Builder
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class GetFavoriteDTO {
    @NotNull
    private String category; // "deposit" (예금) 또는 "installment" (적금)
  }


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 검색 요청 */
  @Builder
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SearchFavoriteDTO {
    @NotNull
    private String category;   // "deposit" 또는 "installment"
    @NotNull
    private String searchKey;  // 검색 키워드 ("bankName" 또는 "prdName")
    @NotNull
    private String searchValue; // 검색값 (사용자가 입력한 값)
  }



  /* (마이페이지) 예금, 적금 즐겨찾기 삭제 요청 (체크박스로 여러 개(1개 이상) 삭제) */
  @Builder
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DeleteFavoriteDTO {
    @NotNull
    private Long[] favoriteIds; // 삭제할 즐겨찾기 ID 배열
  }



}
