/* src/main/java/com/bitcamp/drrate/domain/favorites/service/FavoriteServiceImpl.java */


package com.bitcamp.drrate.domain.favorites.service;


import com.bitcamp.drrate.domain.favorites.entity.Favorites;
import com.bitcamp.drrate.domain.favorites.repository.FavoritesRepository;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.InstallMentOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.ProductsRepository;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoritesService {

  private final FavoritesRepository favoritesRepository;
  private final UsersRepository usersRepository;
  private final ProductsRepository productsRepository;
  private final DepositeOptionsRepository depositeOptionsRepository;
  private final InstallMentOptionsRepository installMentOptionsRepository;

  public Long addFavorite(String userId, Long productId) {
    // 사용자와 상품 조회
    Users user = usersRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
    Products product = productsRepository.findById(productId)
        .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

    // 즐겨찾기 저장
    Favorites favorite = Favorites.builder()
        .user(user)
        .product(product)
        .build();
    Favorites savedFavorite = favoritesRepository.save(favorite);

    return savedFavorite.getId(); // 저장된 즐겨찾기 ID 반환
  }

}
