/* src/main/java/com/bitcamp/drrate/domain/favorites/repository/FavoritesRepository.java */


package com.bitcamp.drrate.domain.favorites.repository;

import com.bitcamp.drrate.domain.favorites.dto.response.FavoriteListDTO;
import com.bitcamp.drrate.domain.favorites.dto.response.FavoritesResponseDTO;
import com.bitcamp.drrate.domain.favorites.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

  boolean existsByUserIdAndProductId(Long faUserId, Long faPrdId);

  void deleteByUserIdAndProductId(Long faUserId, Long faPrdId);


  /* 마이페이지; 예금 목록 조회 */
  @Query("SELECT new com.bitcamp.drrate.domain.favorites.dto.response.FavoriteListDTO(\n" +
      "p.id, f.id, p.bankLogo, p.bankName, p.prdName, MAX(d.basicRate), MAX(d.spclRate)) " +
      "FROM Favorites f " +
      "JOIN f.product p " +
      "JOIN DepositeOptions d ON p.id = d.products.id " +
      "WHERE f.user.id = :faUserId " +
      "GROUP BY p.id, f.id, p.bankLogo, p.bankName, p.prdName " +
      "ORDER BY f.id DESC")
  List<FavoriteListDTO> findDepositsByUserId(@Param("faUserId") Long faUserId);


  /* 마이페이지; 적금 목록 조회 */
  @Query("SELECT new com.bitcamp.drrate.domain.favorites.dto.response.FavoriteListDTO(\n" +
      "p.id, f.id, p.bankLogo, p.bankName, p.prdName, MAX(i.basicRate), MAX(i.spclRate)) " +
      "FROM Favorites f " +
      "JOIN f.product p " +
      "JOIN InstallMentOptions i ON p.id = i.products.id " +
      "WHERE f.user.id = :faUserId " +
      "GROUP BY p.id, f.id, p.bankLogo, p.bankName, p.prdName " +
      "ORDER BY f.id DESC")
  List<FavoriteListDTO> findInstallmentsByUserId(@Param("faUserId") Long faUserId);



  /* 마이페이지; 예금 검색 */
  @Query("SELECT new com.bitcamp.drrate.domain.favorites.dto.response.FavoriteListDTO(\n" +
      "p.id, f.id, p.bankLogo, p.bankName, p.prdName, MAX(d.basicRate), MAX(d.spclRate)) " +
      "FROM Favorites f " +
      "JOIN f.product p " +
      "JOIN DepositeOptions d ON p.id = d.products.id " +
      "WHERE f.user.id = :faUserId AND ((:searchKey = 'bankName' AND p.bankName LIKE %:searchValue%) OR \n" +
      "(:searchKey = 'prdName' AND p.prdName LIKE %:searchValue%)) " +
      "GROUP BY p.id, f.id, p.bankLogo, p.bankName, p.prdName " +
      "ORDER BY f.id DESC")
  List<FavoriteListDTO> searchDepositsByUserId(@Param("faUserId") Long faUserId,
                                               @Param("searchKey") String searchKey,
                                               @Param("searchValue") String searchValue);




  /* 마이페이지; 적금 검색 */
  @Query("SELECT new com.bitcamp.drrate.domain.favorites.dto.response.FavoriteListDTO(\n" +
      "p.id, f.id, p.bankLogo, p.bankName, p.prdName, MAX(i.basicRate), MAX(i.spclRate)) " +
      "FROM Favorites f " +
      "JOIN f.product p " +
      "JOIN InstallMentOptions i ON p.id = i.products.id " +
      "WHERE f.user.id = :faUserId AND ((:searchKey = 'bankName' AND p.bankName LIKE %:searchValue%) OR \n" +
      "(:searchKey = 'prdName' AND p.prdName LIKE %:searchValue%)) " +
      "GROUP BY p.id, f.id, p.bankLogo, p.bankName, p.prdName " +
      "ORDER BY f.id DESC")
  List<FavoriteListDTO> searchInstallmentsByUserId(@Param("faUserId") Long faUserId,
                                                   @Param("searchKey") String searchKey,
                                                   @Param("searchValue") String searchValue);


  /* 마이페이지; 즐겨찾기 배열 삭제 */
  void deleteById(Long favoriteId);
}
