/* src/main/java/com/bitcamp/drrate/domain/favorites/dto/request/FavoritesRequestDTO.java */

package com.bitcamp.drrate.domain.favorites.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritesRequestDTO {
  private String faUserId; // 즐겨찾기 사용자 id
  private Long faPrdId; // 즐겨찾기 예금/적금 상품 id
}
