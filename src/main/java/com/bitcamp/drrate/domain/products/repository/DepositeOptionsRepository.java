package com.bitcamp.drrate.domain.products.repository;

import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositeOptionsRepository extends JpaRepository<DepositeOptions, Long> {

    List<DepositeOptions> findByProductsId(Long prdId);
}
