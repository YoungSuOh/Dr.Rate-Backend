/* src/main/java/com/bitcamp/drrate/domain/favorites/dto/request/FavoritesRequestDTO.java */

package com.bitcamp.drrate.domain.favorites.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritesRequestDTO {
  /**
   * 즐겨찾기 등록 요청
   */
  @Builder
  @Getter
  @AllArgsConstructor
  public static class AddFavoriteDTO {
    @NotNull
    private String faUserId; // 사용자 ID

    @NotNull
    private Long faPrdId; // 상품 ID
  }

  /**
   * 즐겨찾기 취소 요청
   */
  @Builder
  @Getter
  @AllArgsConstructor
  public static class RemoveFavoriteDTO {
    @NotNull
    private Long favoriteId; // 즐겨찾기 ID
  }

  /**
   * 즐겨찾기 삭제 요청 (체크박스로 여러 개(1개 이상) 삭제)
   */
  @Builder
  @Getter
  @AllArgsConstructor
  public static class DeleteFavoriteDTO {
    @NotNull
    private Long[] favoriteIds; // 삭제할 즐겨찾기 ID 배열
  }
}
