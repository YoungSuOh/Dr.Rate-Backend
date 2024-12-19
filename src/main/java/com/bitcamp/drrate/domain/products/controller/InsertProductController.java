package com.bitcamp.drrate.domain.products.controller;

import com.bitcamp.drrate.domain.products.service.InsertProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="api/products")
@RequiredArgsConstructor
public class InsertProductController {

    private final InsertProductService insertProductService;

    @Value("${api.depositUrl}")
    private String depositUrl;

    @Value("${api.savingUrl}")
    private String savingUrl;

    // 예금
    @GetMapping(value = "insertDep")
    public void insertDep() {
        insertProductService.insertProductData("d", depositUrl, true);  // 예금 (deposit)
    }

    // 적금
    @GetMapping(value = "insertIns")
    public void insertIns() {
        insertProductService.insertProductData("i", savingUrl, false);  // 적금 (installment)
    }

}
