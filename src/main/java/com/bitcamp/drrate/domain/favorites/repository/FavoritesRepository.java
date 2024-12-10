/* src/main/java/com/bitcamp/drrate/domain/favorites/repository/FavoritesRepository.java */


package com.bitcamp.drrate.domain.favorites.repository;

import com.bitcamp.drrate.domain.favorites.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

  boolean existsByUserIdAndProductId(Long userId, Long productId);

  void deleteByUserIdAndProductId(Long userId, Long productId);
}
