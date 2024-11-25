package com.bitcamp.drrate.domain.products.service;

import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import com.bitcamp.drrate.domain.products.entity.InstallMentOptions;
import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.InstallMentOptionsRepository;
import com.bitcamp.drrate.domain.products.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService{
    private final ProductsRepository productsRepository;
    private final DepositeOptionsRepository depositeOptionsRepository;

    private final InstallMentOptionsRepository installMentOptionsRepository;

    @Override
    public Map<String, Object> getOneProduct(String prd_id) {
        Optional<Products> prodcut = productsRepository.findById(Long.parseLong(prd_id));

        List<DepositeOptions> dep_options = depositeOptionsRepository.findByProductsId(Long.parseLong(prd_id));
        List<InstallMentOptions> ins_options = installMentOptionsRepository.findByProductsId(Long.parseLong(prd_id));

        Map<String, Object> map = new HashMap<>();
        map.put("product", prodcut);

        if(dep_options != null && !dep_options.isEmpty()){
            map.put("options", dep_options);
        }else if(ins_options != null && !ins_options.isEmpty()){
            map.put("options", ins_options);
        }

        return map;
    }
}
