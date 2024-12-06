/* src/main/java/com/bitcamp/drrate/domain/favorites/dto/response/FavoritesResponseDTO.java */

package com.bitcamp.drrate.domain.favorites.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritesResponseDTO {

  /**
   * 즐겨찾기 목록 조회 응답 (예금/적금 공통)
   */
  @Builder
  @Getter
  @AllArgsConstructor
  public static class FavoriteListDTO {
    private Long favoriteId;    // 즐겨찾기 ID
    private String bankLogo;    // 은행 로고
    private String bankName;    // 은행 이름
    private String prdName;     // 상품 이름
    private BigDecimal basicRate;   // 기본 금리
    private BigDecimal spclRate;    // 최고 금리
  }

  /**
   * 즐겨찾기 등록/취소 응답
   */
  @Builder
  @Getter
  @AllArgsConstructor
  public static class FavoriteActionResponseDTO {
    private Long favoriteId; // 즐겨찾기 ID
    private String message; // 응답 메시지 (등록 성공/취소 성공 등)
  }

}

