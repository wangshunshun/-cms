package com.admin.service.impl;

import com.admin.dto.ProductDTO;
import com.admin.dto.ProductQueryDTO;
import com.admin.entity.Product;
import com.admin.mapper.ProductMapper;
import com.admin.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Transactional
    public void saveProduct(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        save(product);
    }

    @Override
    @Transactional
    public void updateProduct(ProductDTO productDTO) {
        Product product = getById(productDTO.getId());
        if (product != null) {
            BeanUtils.copyProperties(productDTO, product);
            updateById(product);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        removeById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return getById(id);
    }

    @Override
    public Page<Map<String, Object>> getProductPage(ProductQueryDTO queryDTO) {
        Page<Product> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            wrapper.like(Product::getName, queryDTO.getName());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, queryDTO.getCategoryId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Product::getStatus, queryDTO.getStatus());
        }

        page(page, wrapper);

        Page<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (Product product : page.getRecords()) {
            records.add(toMap(product));
        }
        result.setRecords(records);
        return result;
    }

    @Override
    public Map<String, Object> getProductInfo(Long id) {
        Product product = getById(id);
        return product == null ? new HashMap<>() : toMap(product);
    }

    private Map<String, Object> toMap(Product product) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", product.getId());
        result.put("name", product.getName());
        result.put("description", product.getDescription());
        result.put("price", product.getPrice());
        result.put("stock", product.getStock());
        result.put("categoryId", product.getCategoryId());
        result.put("status", product.getStatus());
        result.put("image", product.getImage());
        result.put("createdAt", product.getCreatedAt());
        result.put("updatedAt", product.getUpdatedAt());
        return result;
    }
}
