package com.admin.controller;

import com.admin.dto.ProductDTO;
import com.admin.dto.ProductQueryDTO;
import com.admin.entity.Product;
import com.admin.service.ProductService;
import com.admin.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<Map<String, Object>> getProductList(ProductQueryDTO queryDTO) {
        Page<Map<String, Object>> pageResult = productService.getProductPage(queryDTO);
        long total = pageResult.getTotal();
        if (total == 0 && pageResult.getRecords() != null) {
            total = pageResult.getRecords().size();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("items", pageResult.getRecords());
        data.put("total", total);
        data.put("page", pageResult.getCurrent());
        data.put("pageSize", pageResult.getSize());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getProductDetail(@PathVariable Long id) {
        Map<String, Object> productInfo = productService.getProductInfo(id);
        if (productInfo == null || productInfo.isEmpty()) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(productInfo);
    }

    @PostMapping
    public Result<Void> createProduct(@RequestBody ProductDTO productDTO) {
        productService.saveProduct(productDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        productDTO.setId(id);
        productService.updateProduct(productDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        Integer status = payload.get("status");
        if (status == null) {
            return Result.error(400, "status 不能为空");
        }
        product.setStatus(status);
        productService.updateById(product);
        return Result.success();
    }
}
