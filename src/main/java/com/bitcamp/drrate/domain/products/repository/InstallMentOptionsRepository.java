package com.bitcamp.drrate.domain.products.repository;

import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallMentOptionsRepository extends JpaRepository<InstallMentOptions, Long> {

    List<InstallMentOptions> findByProductsId(Long prd_id);
}
