/* src/main/java/com/bitcamp/drrate/domain/favorites/dto/response/FavoritesResponseDTO.java */

package com.bitcamp.drrate.domain.favorites.dto.response;

import lombok.*;
import java.math.BigDecimal;

public class FavoritesResponseDTO {

  /* ProductDetailPage; 즐겨찾기 등록, 취소 응답 */
  @Builder
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ProductFavoriteActionDTO {
    private String message; // 응답 메시지 (등록 성공/취소 성공 등)
  }



  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 삭제 응답 (체크박스로 여러 개(1개 이상) 삭제) */
  @Builder
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FavoriteActionDTO {
    private String message; // 응답 메시지 (등록 성공/취소 성공 등)
  }

}

