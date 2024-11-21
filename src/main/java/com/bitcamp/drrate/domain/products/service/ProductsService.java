package com.bitcamp.drrate.domain.products.service;

import com.bitcamp.drrate.domain.products.entity.Products;

import java.util.Map;
import java.util.Optional;

public interface ProductsService {
    public Map<String, Object> getOneProduct(String prd_id);
}
