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


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 조회 응답 */
  @Builder
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FavoriteListDTO {
    private Long favoriteId;        // 즐겨찾기 ID
    private String bankLogo;        // 은행 로고
    private String bankName;        // 은행 이름
    private String prdName;         // 상품 이름
    private BigDecimal basicRate;   // 기본 금리
    private BigDecimal spclRate;    // 최고 금리
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

