package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.entity.Products;
import com.bitcamp.drrate.domain.products.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@CrossOrigin
@RequestMapping(value="product")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;

    @GetMapping(value="getOneProduct/{id}")
    @ResponseBody
    public Map<String, Object> getOneProduct(@PathVariable(value="id") String prd_id) {
        Map<String, Object> map = productsService.getOneProduct(prd_id);
        return map;
    }
}
