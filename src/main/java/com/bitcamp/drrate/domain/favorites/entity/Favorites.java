/* src/main/java/com/bitcamp/drrate/domain/favorites/entity/Favorites.java */

package com.bitcamp.drrate.domain.favorites.entity;

import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"fa_user_id", "fa_prd_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorites {


  // 즐겨찾기 id
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  // 즐겨찾기 사용자 (외래 키)
  @ManyToOne(cascade = CascadeType.REMOVE) // Users 엔터티와의 관계 설정
  @JoinColumn(name = "fa_user_id", nullable = false, referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_favorite_user"))
  private Users user;


  // 즐겨찾기 상품 (외래 키)
  @ManyToOne(cascade = CascadeType.REMOVE) // Products 엔터티와의 관계 설정
  @JoinColumn(name = "fa_prd_id", nullable = false, referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_favorite_product"))
  private Products product;

}
