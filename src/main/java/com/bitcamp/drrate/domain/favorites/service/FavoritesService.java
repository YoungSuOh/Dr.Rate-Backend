/* src/main/java/com/bitcamp/drrate/domain/favorites/service/FavoritesService.java */


package com.bitcamp.drrate.domain.favorites.service;

import com.bitcamp.drrate.domain.favorites.dto.response.FavoriteListDTO;
import com.bitcamp.drrate.domain.favorites.dto.response.FavoritesResponseDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface FavoritesService {

  boolean isFavorite(Long faUserId, Long faPrdId);

  void addFavorite(Long faUserId, Long faPrdId);

  void cancelFavorite(Long faUserId, Long faPrdId);

  List<FavoriteListDTO> getFavoriteList(Long faUserId, String category);

  List<FavoriteListDTO> searchFavoriteList(Long faUserId, String category, String searchKey, String searchValue);

  void deleteFavoriteList(Long faUserId, @NotNull Long[] favoriteIds);
}
