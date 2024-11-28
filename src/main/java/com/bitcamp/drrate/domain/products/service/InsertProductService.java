package com.bitcamp.drrate.domain.products.service;

import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;

public interface InsertProductService {
    public void insertProductData(String d, String depositUrl, boolean b);
}
