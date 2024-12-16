/* src/main/java/com/bitcamp/drrate/domain/favorites/service/FavoritesService.java */


package com.bitcamp.drrate.domain.favorites.service;

public interface FavoritesService {

  boolean isFavorite(Long faUserId, Long faPrdId);

  void addFavorite(Long faUserId, Long faPrdId);

  void cancelFavorite(Long faUserId, Long faPrdId);
}
