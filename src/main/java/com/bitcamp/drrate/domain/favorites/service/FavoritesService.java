/* src/main/java/com/bitcamp/drrate/domain/favorites/service/FavoriteService.java */


package com.bitcamp.drrate.domain.favorites.service;

public interface FavoritesService {

  public boolean isFavorite(Long faUserId, Long faPrdId);
  
  public void addFavorite(Long faUserId, Long faPrdId);

  public void removeFavorite(Long faUserId, Long faPrdId);
}
