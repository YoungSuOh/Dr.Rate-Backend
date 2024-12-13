package com.bitcamp.drrate.domain.products.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bitcamp.drrate.domain.products.entity.Products;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    /* 상품등록 - 중복확인 */
    boolean existsByPrdCo(String prdCo);
    
    //241211 카테고리로 상품 조회 - 오혜진
    List<Products> findByCtg(String ctg);
    
    //241211 모든 제품과 예금 옵션을 조인하여 가져오는 쿼리
    @Query("SELECT p FROM Products p LEFT JOIN FETCH p.depOptions")
    List<Products> findAllWithDepOptions(); // 제품과 관련된 예금 옵션을 모두 가져옴
}
