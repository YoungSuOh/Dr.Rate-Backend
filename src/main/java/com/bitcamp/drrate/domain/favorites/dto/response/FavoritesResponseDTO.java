/* src/main/java/com/bitcamp/drrate/domain/favorites/dto/response/FavoritesResponseDTO.java */

package com.bitcamp.drrate.domain.favorites.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritesResponseDTO {

  private Long id; // 즐겨찾기 id
  private String faUserId; // 즐겨찾기 사용자 id
  private Long faPrdId; // 즐겨찾기 예금/적금 상품 id
}

