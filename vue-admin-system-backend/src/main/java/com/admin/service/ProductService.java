package com.admin.service;

import com.admin.dto.ProductDTO;
import com.admin.dto.ProductQueryDTO;
import com.admin.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ProductService extends IService<Product> {
    void saveProduct(ProductDTO productDTO);
    void updateProduct(ProductDTO productDTO);
    void deleteProduct(Long id);
    Product getProductById(Long id);
    Page<Map<String, Object>> getProductPage(ProductQueryDTO queryDTO);
    Map<String, Object> getProductInfo(Long id);
}