/* src/main/java/com/bitcamp/drrate/domain/favorites/dto/request/FavoritesRequestDTO.java */

package com.bitcamp.drrate.domain.favorites.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritesRequestDTO {

  /* ProductDetailPage; 즐겨찾기 조회, 등록, 취소 요청 */
  @Builder
  @Getter
  @AllArgsConstructor
  public class ProductFavoriteDTO {
    @NotNull
    private Long faPrdId; // 상품 ID
  }


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 조회 요청 */
  // favorites 테이블의 favoriteId
  // products 테이블의 bankLogo
  // products 테이블의 bankName
  // products 테이블의 prdName
  
  // 예금 즐겨찾기의 경우
  // dep_options 테이블의 basic_rate
  // dep_options 테이블의 spcl_rate

  // 적금 즐겨찾기의 경우
  // ins_options 테이블의 basic_rate
  // ins_options 테이블의 spcl_rate



  /* (마이페이지) 예금, 적금 즐겨찾기 삭제 요청 (체크박스로 여러 개(1개 이상) 삭제) */
  @Builder
  @Getter
  @AllArgsConstructor
  public static class DeleteFavoriteDTO {
    @NotNull
    private Long[] favoriteIds; // 삭제할 즐겨찾기 ID 배열
  }



}
