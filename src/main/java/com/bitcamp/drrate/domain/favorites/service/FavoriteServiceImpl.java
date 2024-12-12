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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoritesService {

  private final FavoritesRepository favoritesRepository;
  private final UsersRepository usersRepository;
  private final ProductsRepository productsRepository;
  private final DepositeOptionsRepository depositeOptionsRepository;
  private final InstallMentOptionsRepository installMentOptionsRepository;


  /* ProductDetailPage; 즐겨찾기 조회 */
  @Override
  public boolean isFavorite(Long faUserId, Long faPrdId) {
    return favoritesRepository.existsByUserIdAndProductId(faUserId, faPrdId);
  }


  /* ProductDetailPage; 즐겨찾기 등록 */
  @Override
  public void  addFavorite(Long faUserId, Long faPrdId) {
    // Users 및 Products 엔티티를 조회
    Users user = usersRepository.findById(faUserId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
    Products product = productsRepository.findById(faPrdId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품 ID입니다."));

    // Favorites 엔티티 생성
    Favorites favorite = Favorites.builder()
        .user(user)
        .product(product)
        .build();

    // 즐겨찾기 등록(저장)
    favoritesRepository.save(favorite);
  }


  /* ProductDetailPage; 즐겨찾기 취소 */
  @Override
  @Transactional
  public void removeFavorite(Long faUserId, Long faPrdId) {
    favoritesRepository.deleteByUserIdAndProductId(faUserId, faPrdId);
  }


}
