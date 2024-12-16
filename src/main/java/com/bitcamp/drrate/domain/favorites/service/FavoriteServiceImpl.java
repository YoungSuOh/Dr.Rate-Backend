/* src/main/java/com/bitcamp/drrate/domain/favorites/service/FavoriteServiceImpl.java */


package com.bitcamp.drrate.domain.favorites.service;


import com.bitcamp.drrate.domain.favorites.dto.response.FavoriteListDTO;
import com.bitcamp.drrate.domain.favorites.dto.response.FavoritesResponseDTO;
import com.bitcamp.drrate.domain.favorites.entity.Favorites;
import com.bitcamp.drrate.domain.favorites.repository.FavoritesRepository;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.InstallMentOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.ProductsRepository;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.FavoritesServiceExceptionHandler;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    try {
      // 사용자 ID 검증
      usersRepository.findById(faUserId)
          .orElseThrow(() -> new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INVALID_USER_ID));

      // 상품 ID 검증
      productsRepository.findById(faPrdId)
          .orElseThrow(() -> new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INVALID_PRODUCT_ID));

      // 즐겨찾기 존재 여부 확인
      return favoritesRepository.existsByUserIdAndProductId(faUserId, faPrdId);
    } catch (FavoritesServiceExceptionHandler e) {
      throw e; // 발생한 예외를 그대로 전달
    } catch (Exception e) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_QUERY_FAILED);
    }
  }


  /* ProductDetailPage; 즐겨찾기 등록 */
  @Override
  public void addFavorite(Long faUserId, Long faPrdId) {

    // Users 및 Products 엔티티를 조회
    Users user = usersRepository.findById(faUserId)
        .orElseThrow(() -> new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INVALID_USER_ID));
    Products product = productsRepository.findById(faPrdId)
        .orElseThrow(() -> new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INVALID_PRODUCT_ID));

    // 이미 즐겨찾기에 등록되어 있는지 확인
    if (favoritesRepository.existsByUserIdAndProductId(faUserId, faPrdId)) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_ALREADY_EXISTS);
    }

    try {
      // Favorites 엔티티 생성
      Favorites favorite = Favorites.builder()
          .user(user)
          .product(product)
          .build();

      // 즐겨찾기 등록(저장)
      favoritesRepository.save(favorite);
    } catch (Exception e) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INSERT_FAILED);
    }

  }


  /* ProductDetailPage; 즐겨찾기 취소 */
  @Override
  @Transactional
  public void cancelFavorite(Long faUserId, Long faPrdId) {
    // Users 및 Products 엔티티를 조회
    Users user = usersRepository.findById(faUserId)
        .orElseThrow(() -> new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INVALID_USER_ID));
    Products product = productsRepository.findById(faPrdId)
        .orElseThrow(() -> new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INVALID_PRODUCT_ID));

    if (!favoritesRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_NOT_FOUND);
    }

    try {
      favoritesRepository.deleteByUserIdAndProductId(user.getId(), product.getId());
    } catch (Exception e) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_DELETE_FAILED);
    }
  }


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 조회 */
  @Override
  public List<FavoriteListDTO> getFavoriteList(Long faUserId, String category) {
    try {
      // 예금 카테고리 조회
      if ("deposit".equalsIgnoreCase(category)) {
        return favoritesRepository.findDepositsByUserId(faUserId);
      }
      // 적금 카테고리 조회
      else if ("installment".equalsIgnoreCase(category)) {
        return favoritesRepository.findInstallmentsByUserId(faUserId);
      }
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_QUERY_FAILED);
    } catch (Exception e) {
      e.printStackTrace();
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_QUERY_FAILED);
    }
  }


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 검색 */
  @Override
  public List<FavoriteListDTO> searchFavoriteList(Long faUserId, String category, String searchKey, String searchValue) {
    try {
      if ("deposit".equalsIgnoreCase(category)) {
        return favoritesRepository.searchDepositsByUserId(faUserId, searchKey, searchValue);
      } else if ("installment".equalsIgnoreCase(category)) {
        return favoritesRepository.searchInstallmentsByUserId(faUserId, searchKey, searchValue);
      }
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_SEARCH_FAILED);
    } catch (Exception e) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_SEARCH_FAILED);
    }
  }


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 삭제 */
  @Override
  public void deleteFavoriteList(Long faUserId, @NotNull Long[] favoriteIds) {
    List<Long> notFoundIds = new ArrayList<>();
    List<Long> validIds = new ArrayList<>(); // 유효한 ID 수집

    // ID 검증
    for (Long favoriteId : favoriteIds) {
      Optional<Favorites> optionalFavorite = favoritesRepository.findById(favoriteId);

      // 존재하지 않는 ID 처리
      if (optionalFavorite.isEmpty()) {
        notFoundIds.add(favoriteId);
        continue;
      }

      Favorites favorite = optionalFavorite.get();

      // 사용자 ID 불일치 처리
      if (!favorite.getUser().getId().equals(faUserId)) {
        throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INVALID_USER_ID);
      }

      validIds.add(favoriteId); // 유효한 ID만 수집
    }

    // 모든 ID가 존재하지 않을 경우
    if (notFoundIds.size() == favoriteIds.length) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_NO_RESULTS);
    }

    // 일부 ID만 존재하지 않는 경우
    if (!notFoundIds.isEmpty()) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_PARTIAL_DELETE_FAILED);
    }

    // 유효한 ID 삭제
    for (Long favoriteId : validIds) {
      favoritesRepository.deleteById(favoriteId);
    }
  }

}
