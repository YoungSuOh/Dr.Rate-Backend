/* src/main/java/com/bitcamp/drrate/domain/favorites/dto/response/FavoriteListDTO.java */

package com.bitcamp.drrate.domain.favorites.dto.response;

import lombok.*;
import java.math.BigDecimal;

/* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 조회 응답 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteListDTO {
  private Long favoriteId;        // 즐겨찾기 ID
  private String bankLogo;        // 은행 로고
  private String bankName;        // 은행 이름
  private String prdName;         // 상품 이름
  private BigDecimal basicRate;   // 기본 금리
  private BigDecimal spclRate;    // 최고 금리
}
