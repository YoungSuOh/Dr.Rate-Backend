package com.bitcamp.drrate.domain.products.repository;

import com.bitcamp.drrate.domain.products.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    boolean existsByPrdCo(String prdCo);
}
