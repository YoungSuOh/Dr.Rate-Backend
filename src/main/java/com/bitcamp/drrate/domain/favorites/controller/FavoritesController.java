/* src/main/java/com/bitcamp/drrate/domain/favorites/controller/FavoritesController.java */

package com.bitcamp.drrate.domain.favorites.controller;


import com.bitcamp.drrate.domain.favorites.dto.request.FavoritesRequestDTO;
import com.bitcamp.drrate.domain.favorites.dto.response.FavoritesResponseDTO;
import com.bitcamp.drrate.domain.favorites.service.FavoritesService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.service.UsersService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping(value="/api/favorite")
@RequiredArgsConstructor
public class FavoritesController {

  private final UsersService usersService;
  private final FavoritesService favoritesService;


  /* ProductDetailPage; 즐겨찾기 조회 */
  @GetMapping("/checkFavorite/{prdId}")
  public ApiResponse<Boolean> checkFavorite(
      @AuthenticationPrincipal CustomUserDetails userDetails, // JWT; 인증된 사용자 정보 가져오기
      @PathVariable Long prdId // URL 경로에서 파라미터를 가져옴
  ) {
    // 1. 사용자 ID(PK)를 JWT에서 추출 & 경로 변수로 전달받은 상품 ID를 설정
    Long faUserId = usersService.getUserId(userDetails);
    Long faPrdId = prdId;

    // 2. 서비스 호출: 추출한 faUserId와 요청으로 전달된 faPrdId를 FavoritesService 에 전달
    boolean isFavorite = favoritesService.isFavorite(faUserId, faPrdId);

    // 3. HTTP 200 OK 응답: 즐겨찾기 여부를 ApiResponse 객체로 클라이언트에 반환
    return ApiResponse.onSuccess(isFavorite, SuccessStatus.FAVORITE_QUERY_SUCCESS);
  }



  /* ProductDetailPage; 즐겨찾기 등록 */
  @PostMapping("/addFavorite")
  public ApiResponse<HttpStatus> addFavorite(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid FavoritesRequestDTO.ProductFavoriteDTO request
  ) {

    Long faUserId = usersService.getUserId(userDetails);
    Long faPrdId = request.getPrdId(); // 요청으로 전달된 상품 ID(request.getFaPrdId())를 faPrdId에 저장

    favoritesService.addFavorite(faUserId, faPrdId);

    return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.FAVORITE_ADD_SUCCESS);
  }



  /* ProductDetailPage; 즐겨찾기 취소 */
  @DeleteMapping("/cancelFavorite/{prdId}")
  @Transactional
  public ApiResponse<HttpStatus> cancelFavorite(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long prdId // URL 경로에서 상품 ID를 가져옴
  ) {
    Long faUserId = usersService.getUserId(userDetails);
    Long faPrdId = prdId;

    favoritesService.cancelFavorite(faUserId, faPrdId);

    return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.FAVORITE_DELETE_SUCCESS);
  }







  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 조회 */
  // public viewFavorite {}


  /* MyDepositPage, MyInstallmentPage; 즐겨찾기 목록 삭제 */
  // public deleteFavorite {}
  // 1 개 삭제 혹은 여러 개 삭제
  // 여러 개 삭제 가능하도록 체크박스 입력 받음, 그 체크박스 상품 아이디를 배열로 받아야함
  
  


}

//  @PostMapping(value="favoriteInsert/{id}")
//  public ResponseEntity<Void> favoriteInsert(@PathVariable(value="id") String prd_id,
//                                             @RequestHeader(value="userId") String user_id) {
//    // 데이터 처리 로직 추가
//    System.out.println("Product ID: " + prd_id);
//    System.out.println("User ID: " + user_id);
//
//    return ResponseEntity.ok().build(); // HTTP 200 OK 응답
//  }
