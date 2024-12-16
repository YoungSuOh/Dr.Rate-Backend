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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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



  // getFavoriteList(), searchFavoriteList()
  // favorites 테이블의 favoriteId
  // products 테이블의 bankLogo
  // products 테이블의 bankName
  // products 테이블의 prdName

  // 예금 즐겨찾기의 경우
  // dep_options 테이블의 basic_rate
  // dep_options 테이블의 spcl_rate

  // 적금 즐겨찾기의 경우
  // ins_options 테이블의 basic_rate
  // ins_options 테이블의 spcl_rate

  @Override
  public List<FavoriteListDTO> getFavoriteList(Long faUserId, String category) {
    try {
      System.out.println("******** User ID: " + faUserId);
      System.out.println("******** User ID:  Category: " + category);
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

  @Override
  public void deleteFavoriteList(Long faUserId, @NotNull Long[] favoriteIds) {
    List<Long> failedIds = new ArrayList<>();
    for (Long favoriteId : favoriteIds) {
      try {
        Favorites favorite = favoritesRepository.findById(favoriteId)
            .orElseThrow(() -> new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_NOT_FOUND));

        if (!favorite.getUser().getId().equals(faUserId)) {
          throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_INVALID_USER_ID);
        }

        favoritesRepository.deleteById(favoriteId);
      } catch (Exception e) {
        failedIds.add(favoriteId);
      }
    }

    if (!failedIds.isEmpty()) {
      throw new FavoritesServiceExceptionHandler(ErrorStatus.FAVORITE_PARTIAL_DELETE_FAILED);
    }
  }


}